package com.yunshang.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunshang.auth.mapper.SysMeetingMapper;
import com.yunshang.auth.service.MeetingRoomService;
import com.yunshang.auth.service.SysMeetingService;
import com.yunshang.auth.service.meeting.MeetingExpiredService;
import com.yunshang.common.exception.YunshangException;
import com.yunshang.common.result.ResultCodeEnum;
import com.yunshang.model.system.SysMeeting;
import com.yunshang.model.system.SysMeetingRoom;
import com.yunshang.security.custom.LoginUserInfoHelper;
import com.yunshang.vo.system.SysMeetingQueryVo;
import com.yunshang.vo.system.SysMeetingVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SysMeetingServiceImpl implements SysMeetingService {

    @Autowired
    private SysMeetingMapper sysMeetingMapper;

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private SysMeetingService sysMeetingService;

    @Autowired
    private MeetingRoomService meetingRoomService;

    @Autowired
    private MeetingExpiredService meetingExpiredService;

    @Override
    public Boolean checkMeeting(SysMeeting sysMeeting) {
        // 构建查询条件：检查相同会议室且时间交叉的会议
        // 时间交叉条件：现有会议结束时间 > 当前会议开始时间 且 现有会议开始时间 < 当前会议结束时间
        // 排除已删除的会议记录
        LambdaQueryWrapper<SysMeeting> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMeeting::getMeetingRoomId, sysMeeting.getMeetingRoomId())
                .eq(SysMeeting::getIsDeleted, 0)
                .gt(SysMeeting::getEnd, sysMeeting.getStart())
                .lt(SysMeeting::getStart, sysMeeting.getEnd());

        // 如果是更新操作，排除当前会议自身
        if (sysMeeting.getId() != null) {
            queryWrapper.ne(SysMeeting::getId, sysMeeting.getId());
        }

        // 统计满足条件的会议数量
        Long count = sysMeetingMapper.selectCount(queryWrapper);

        return count == 0;
    }

    @Override
    public void addMeeting(SysMeeting sysMeeting) {
        // 校验时间：开始时间必须小于结束时间
        if (sysMeeting.getStart() == null || sysMeeting.getEnd() == null) {
            throw new YunshangException(ResultCodeEnum.DATA_ERROR);
        }
        if (sysMeeting.getStart() >= sysMeeting.getEnd()) {
            throw new YunshangException(ResultCodeEnum.DATA_ERROR);
        }

        //设置用户id
        Long userId = LoginUserInfoHelper.getUserId();
        if (userId == null) {
            throw new YunshangException(ResultCodeEnum.LOGIN_ERROR);
        }
        sysMeeting.setUserId(userId);

        // 获取会议室 ID 作为锁的对象
        Long meetingRoomId = sysMeeting.getMeetingRoomId();
        if (meetingRoomId == null) {
            throw new YunshangException(ResultCodeEnum.DATA_ERROR);
        }

        // 使用 Redisson 分布式锁，锁的对象是会议室 ID
        String lockKey = "meeting_room_lock:" + meetingRoomId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁，等待 10 秒，锁过期时间 30 秒
            boolean isLock = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!isLock) {
                throw new YunshangException(ResultCodeEnum.SERVICE_ERROR);
            }

            // 检查会议是否与已有会议交叉
            if (!checkMeeting(sysMeeting)) {
                throw new YunshangException(ResultCodeEnum.MEETING_TIME_CONFLICT);
            }

            // 不交叉则插入会议
            int result = sysMeetingMapper.insert(sysMeeting);

            // 添加会议过期时间到Redis ZSet
            if (result > 0) {
                meetingExpiredService.addMeetingExpired(sysMeeting.getId(), sysMeeting.getEnd());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new YunshangException(ResultCodeEnum.SERVICE_ERROR);
        } finally {
            // 确保释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public void updateMeeting(SysMeeting sysMeeting) {
        // 校验会议ID
        if (sysMeeting.getId() == null) {
            throw new YunshangException(ResultCodeEnum.DATA_ERROR);
        }

        // 获取会议室 ID 作为锁的对象
        Long meetingRoomId = sysMeeting.getMeetingRoomId();
        if (meetingRoomId == null) {
            throw new YunshangException(ResultCodeEnum.DATA_ERROR);
        }

        // 使用 Redisson 分布式锁，锁的对象是会议室 ID
        String lockKey = "meeting_room_lock:" + meetingRoomId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁，等待 10 秒，锁过期时间 30 秒
            boolean isLock = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!isLock) {
                throw new YunshangException(ResultCodeEnum.SERVICE_ERROR);
            }

            // 检查会议时间是否与其他会议冲突（checkMeeting已排除自身和已删除记录）
            if (!checkMeeting(sysMeeting)) {
                throw new YunshangException(ResultCodeEnum.MEETING_TIME_CONFLICT);
            }

            // 查询原有会议信息
            SysMeeting existingMeeting = sysMeetingMapper.selectById(sysMeeting.getId());
            if (existingMeeting == null) {
                throw new YunshangException(ResultCodeEnum.DATA_ERROR);
            }

            // 不冲突则更新会议
            sysMeetingMapper.updateById(sysMeeting);

            // 如果会议结束时间被修改，需要更新Redis ZSet中的过期时间
            if (sysMeeting.getEnd() != null && !sysMeeting.getEnd().equals(existingMeeting.getEnd())) {
                // 重置过期状态
                SysMeeting resetOvertime = new SysMeeting();
                resetOvertime.setId(sysMeeting.getId());
                resetOvertime.setIsOvertime(0);
                sysMeetingMapper.updateById(resetOvertime);

                // 更新ZSet中的过期时间
                meetingExpiredService.updateMeetingExpired(sysMeeting.getId(), sysMeeting.getEnd());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new YunshangException(ResultCodeEnum.SERVICE_ERROR);
        } finally {
            // 确保释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public void removeMeeting(Long id) {
        // 校验会议ID
        if (id == null) {
            throw new YunshangException(ResultCodeEnum.DATA_ERROR);
        }

        // 校验会议是否存在
        SysMeeting meeting = sysMeetingMapper.selectById(id);
        if (meeting == null) {
            throw new YunshangException(ResultCodeEnum.DATA_ERROR);
        }

        // 逻辑删除（MyBatis Plus @TableLogic 自动将 is_deleted 设为 1）
        // 无需加锁：逻辑删除不占用时间，checkMeeting已自动过滤已删除记录
        sysMeetingMapper.deleteById(id);

        // 从Redis ZSet中移除会议过期记录
        meetingExpiredService.removeMeetingExpired(id);
    }

    @Override
    public Page<SysMeetingVo> findByUserId(Page<SysMeeting> pageParam) {
        // 从ThreadLocal获取当前用户ID
        Long userId = LoginUserInfoHelper.getUserId();
        if (userId == null) {
            throw new YunshangException(ResultCodeEnum.LOGIN_ERROR);
        }

        // 查询当前用户的未删除会议
        LambdaQueryWrapper<SysMeeting> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMeeting::getUserId, userId)
                .eq(SysMeeting::getIsDeleted, 0)
                .orderByDesc(SysMeeting::getStart);

        Page<SysMeeting> meetingPage = sysMeetingMapper.selectPage(pageParam, queryWrapper);
        return convertToVoPage(meetingPage);
    }

    @Override
    public Page<SysMeetingVo> findAllByPage(Page<SysMeeting> pageParam) {
        // 查询所有未删除的会议
        LambdaQueryWrapper<SysMeeting> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMeeting::getIsDeleted, 0)
                .orderByDesc(SysMeeting::getStart);

        Page<SysMeeting> meetingPage = sysMeetingMapper.selectPage(pageParam, queryWrapper);
        return convertToVoPage(meetingPage);
    }

    @Override
    public List<SysMeetingVo> findAll() {
        // 查询所有未删除的会议
        LambdaQueryWrapper<SysMeeting> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMeeting::getIsDeleted, 0)
                .orderByDesc(SysMeeting::getStart);

        List<SysMeeting> meetingList = sysMeetingMapper.selectList(queryWrapper);
        return convertToVoList(meetingList);
    }

    @Override
    public Page<SysMeetingVo> search(Page<SysMeeting> pageParam, SysMeetingQueryVo queryVo) {
        LambdaQueryWrapper<SysMeeting> queryWrapper = new LambdaQueryWrapper<>();

        // 过滤已删除记录
        queryWrapper.eq(SysMeeting::getIsDeleted, 0);

        // 会议名称模糊查询
        if (StringUtils.hasText(queryVo.getMeetingName())) {
            queryWrapper.like(SysMeeting::getMeetingName, queryVo.getMeetingName());
        }

        // 会议室ID精确查询
        if (queryVo.getMeetingRoomId() != null) {
            queryWrapper.eq(SysMeeting::getMeetingRoomId, queryVo.getMeetingRoomId());
        }

        // 开始时间范围查询
        if (queryVo.getStartTime() != null) {
            queryWrapper.ge(SysMeeting::getStart, queryVo.getStartTime());
        }
        if (queryVo.getEndTime() != null) {
            queryWrapper.le(SysMeeting::getStart, queryVo.getEndTime());
        }

        // 按开始时间倒序排序
        queryWrapper.orderByDesc(SysMeeting::getStart);

        Page<SysMeeting> meetingPage = sysMeetingMapper.selectPage(pageParam, queryWrapper);
        return convertToVoPage(meetingPage);
    }

    /**
     * 将SysMeeting列表转换为SysMeetingVo列表
     */
    private List<SysMeetingVo> convertToVoList(List<SysMeeting> meetingList) {
        if (meetingList == null || meetingList.isEmpty()) {
            return List.of();
        }

        // 收集所有会议室ID
        List<Long> roomIds = meetingList.stream()
                .map(SysMeeting::getMeetingRoomId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询会议室并转为Map
        Map<Long, String> roomNumberMap = meetingRoomService.listByIds(roomIds).stream()
                .collect(Collectors.toMap(SysMeetingRoom::getId, SysMeetingRoom::getRoomNumber));

        // 转换
        return meetingList.stream().map(meeting -> {
            SysMeetingVo vo = new SysMeetingVo();
            BeanUtils.copyProperties(meeting, vo);
            vo.setRoomNumber(roomNumberMap.get(meeting.getMeetingRoomId()));
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 将Page<SysMeeting>转换为Page<SysMeetingVo>
     */
    private Page<SysMeetingVo> convertToVoPage(Page<SysMeeting> meetingPage) {
        List<SysMeetingVo> voList = convertToVoList(meetingPage.getRecords());

        Page<SysMeetingVo> voPage = new Page<>(meetingPage.getCurrent(), meetingPage.getSize(), meetingPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }
}
