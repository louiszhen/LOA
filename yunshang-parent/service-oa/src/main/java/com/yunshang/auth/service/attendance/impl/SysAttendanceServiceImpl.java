package com.yunshang.auth.service.attendance.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunshang.auth.config.AttendanceConfig;
import com.yunshang.auth.mapper.attendance.SysAttendanceRecordMapper;
import com.yunshang.auth.mapper.attendance.SysAttendanceStatisticsMapper;
import com.yunshang.auth.service.attendance.SysAttendanceService;
import com.yunshang.model.attendance.AttendanceStatus;
import com.yunshang.model.attendance.ClockType;
import com.yunshang.model.attendance.SysAttendanceRecord;
import com.yunshang.model.attendance.SysAttendanceStatistics;
import com.yunshang.security.custom.LoginUserInfoHelper;
import com.yunshang.vo.attendance.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBitSet;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 考勤服务实现类
 * @author louis
 * @date 2026-03-20
 */
@Slf4j
@Service
public class SysAttendanceServiceImpl extends ServiceImpl<SysAttendanceRecordMapper, SysAttendanceRecord> 
    implements SysAttendanceService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SysAttendanceStatisticsMapper statisticsMapper;

    @Autowired
    private SysAttendanceProducerService producerService;

    /**
     * 打卡
     * @param requestVo 打卡请求
     * @return 打卡结果
     */
    @Override
    public ClockResponseVo clock(ClockRequestVo requestVo) {
        ClockResponseVo response = new ClockResponseVo();
        
        // 获取当前用户信息
        Long userId = LoginUserInfoHelper.getUserId();
        String userName = LoginUserInfoHelper.getUsername();
        
        if (userId == null) {
            response.setSuccess(false);
            response.setMessage("获取用户信息失败");
            return response;
        }

        // 获取打卡时间
        LocalDateTime clockDateTime;
        if (requestVo.getClockTime() != null) {
            clockDateTime = LocalDateTime.ofInstant(requestVo.getClockTime().toInstant(), java.time.ZoneId.systemDefault());
        } else {
            clockDateTime = LocalDateTime.now();
        }
        
        // 格式化日期
        String clockDate = clockDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String clockMonth = clockDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String clockType = requestVo.getClockType();
        
        // 校验打卡类型
        if (!ClockType.MORNING.getCode().equals(clockType) && !ClockType.EVENING.getCode().equals(clockType)) {
            response.setSuccess(false);
            response.setMessage("打卡类型不正确，请输入 morning 或 evening");
            return response;
        }

        // 修复4: 分布式锁防止并发重复打卡
        String lockKey = "attendance:lock:" + userId + ":" + clockDate + ":" + clockType;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean locked = lock.tryLock(3, 5, TimeUnit.SECONDS);
            if (!locked) {
                response.setSuccess(false);
                response.setMessage("操作频繁，请稍后重试");
                return response;
            }

            // 检查是否已打卡
            if (isAlreadyClocked(userId, clockDate, clockType)) {
                response.setSuccess(false);
                response.setMessage(ClockType.MORNING.getCode().equals(clockType) ? "今日上班已打卡" : "今日下班已打卡");
                response.setAlreadyClocked(true);
                return response;
            }

            // 判断打卡状态
            String status = AttendanceStatus.NORMAL.getCode();
            String statusDesc = AttendanceStatus.NORMAL.getDesc();
            LocalTime clockTime = clockDateTime.toLocalTime();

            if (ClockType.MORNING.getCode().equals(clockType)) {
                // 上班打卡判断是否迟到
                // 修复3: 基准改为 LATE_TOLERANCE_TIME(09:30)，而非 MORNING_WORK_TIME(09:00)
                if (clockTime.isAfter(AttendanceConfig.LATE_TOLERANCE_TIME)) {
                    status = AttendanceStatus.LATE.getCode();
                    statusDesc = AttendanceStatus.LATE.getDesc() + "（超过" +
                            java.time.Duration.between(AttendanceConfig.LATE_TOLERANCE_TIME, clockTime).toMinutes() + "分钟）";
                }
            } else {
                // 下班打卡判断是否早退
                if (clockTime.isBefore(AttendanceConfig.EARLY_LEAVE_TOLERANCE_TIME)) {
                    status = AttendanceStatus.EARLY_LEAVE.getCode();
                    statusDesc = AttendanceStatus.EARLY_LEAVE.getDesc() + "（提前" +
                            java.time.Duration.between(clockTime, AttendanceConfig.EVENING_WORK_TIME).toMinutes() + "分钟）";
                }
            }

            // 修复1+2: 使用 bitSet.expire 替代 getBucket().expire，常量引用 AttendanceConfig
            String bitmapKey = buildBitmapKey(clockDate, clockType);
            RBitSet bitSet = redissonClient.getBitSet(bitmapKey);
            bitSet.set(userId, true);
            bitSet.expire(java.time.Duration.ofDays(AttendanceConfig.BITMAP_EXPIRE_DAYS));

            log.info("用户 {} 在 {} {} 打卡，状态: {}", userId, clockDate, clockType, status);

            // 发送消息到队列，由消费者处理MySQL落盘
            AttendanceMessageVo message = new AttendanceMessageVo();
            message.setUserId(userId);
            message.setUserName(userName);
            message.setClockDate(clockDate);
            message.setClockMonth(clockMonth);
            message.setClockType(clockType);
            message.setClockTime(Date.from(clockDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant()));
            message.setStatus(status);
            message.setStatusDesc(statusDesc);
            message.setRemark("通过Redis Bitmap记录打卡");
            producerService.sendAttendanceMessage(message);

            response.setSuccess(true);
            response.setStatus(status);
            response.setStatusDesc(statusDesc);
            response.setClockTime(Date.from(clockDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant()));
            response.setClockType(clockType);
            response.setMessage("打卡成功");
            response.setAlreadyClocked(false);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            response.setSuccess(false);
            response.setMessage("打卡失败，请重试");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return response;
    }

    /**
     * 查询打卡记录列表
     * <p>
     * 权限规则：admin 用户可查询所有人记录；非 admin 只能查询自己的记录，
     * 忽略请求中传入的 userId/userName 参数，强制覆盖为当前登录用户 ID。
     * </p>
     */
    @Override
    public List<SysAttendanceRecord> selectRecordList(AttendanceRecordQueryVo queryVo) {
        String currentUsername = LoginUserInfoHelper.getUsername();
        boolean isAdmin = "admin".equals(currentUsername);
        if (!isAdmin) {
            // 非 admin：强制只查当前用户自己的打卡记录
            Long currentUserId = LoginUserInfoHelper.getUserId();
            queryVo.setUserId(currentUserId);
            queryVo.setUserName(null);
        }
        return baseMapper.selectRecordList(queryVo);
    }

    /**
     * 分页查询打卡记录列表
     * <p>
     * 权限规则与 selectRecordList 一致：admin 可查全部，非 admin 强制只查自己。
     * </p>
     */
    @Override
    public IPage<SysAttendanceRecord> selectRecordPage(long current, long size, AttendanceRecordQueryVo queryVo) {
        String currentUsername = LoginUserInfoHelper.getUsername();
        boolean isAdmin = "admin".equals(currentUsername);
        if (!isAdmin) {
            Long currentUserId = LoginUserInfoHelper.getUserId();
            queryVo.setUserId(currentUserId);
            queryVo.setUserName(null);
        }
        Page<SysAttendanceRecord> page = new Page<>(current, size);
        return baseMapper.selectRecordPage(page, queryVo);
    }

    /**
     * 分页查询考勤统计列表
     */
    @Override
    public IPage<SysAttendanceStatistics> selectStatisticsPage(long current, long size, AttendanceStatisticsQueryVo queryVo) {
        Page<SysAttendanceStatistics> page = new Page<>(current, size);
        return statisticsMapper.selectStatisticsPage(page, queryVo);
    }

    /**
     * 查询考勤统计列表
     */
    @Override
    public List<SysAttendanceStatistics> selectStatisticsList(AttendanceStatisticsQueryVo queryVo) {
        return statisticsMapper.selectStatisticsList(queryVo);
    }

    /**
     * 获取用户指定月考勤统计（Redis 旁路缓存）
     *
     * 读取路径：先查 Redis → 命中直接返回；未命中 → 查 DB → 写入 Redis（TTL 1天）→ 返回
     * 失效路径：AttendanceTask.updateStatistics() 落库后主动删除对应 key
     */
    @Override
    public SysAttendanceStatistics getUserMonthlyStatistics(Long userId, String statMonth) {
        String cacheKey = AttendanceConfig.STATISTICS_CACHE_KEY_PREFIX + userId + ":" + statMonth;
        RBucket<SysAttendanceStatistics> bucket = redissonClient.getBucket(cacheKey);

        // 1. 查缓存
        SysAttendanceStatistics cached = bucket.get();
        if (cached != null) {
            log.debug("考勤统计缓存命中：userId={}, statMonth={}", userId, statMonth);
            return cached;
        }

        // 2. 查数据库
        SysAttendanceStatistics result = statisticsMapper.selectByUserAndMonth(userId, statMonth);

        // 3. 回写缓存（结果为 null 时不缓存，避免缓存穿透）
        if (result != null) {
            bucket.set(result, AttendanceConfig.STATISTICS_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
            log.debug("考勤统计写入缓存：userId={}, statMonth={}", userId, statMonth);
        }

        return result;
    }

    /**
     * 检查用户是否已打卡
     */
    @Override
    public boolean isAlreadyClocked(Long userId, String clockDate, String clockType) {
        // 先查Redis
        boolean redisClocked = getClockStatusFromRedis(userId, clockDate, clockType);
        if (redisClocked) {
            return true;
        }
        
        // Redis没有再查MySQL
        Integer count = baseMapper.checkAlreadyClocked(userId, clockDate, clockType);
        return count != null && count > 0;
    }

    /**
     * 从Redis获取用户打卡状态
     */
    @Override
    public boolean getClockStatusFromRedis(Long userId, String clockDate, String clockType) {
        String bitmapKey = buildBitmapKey(clockDate, clockType);
        RBitSet bitSet = redissonClient.getBitSet(bitmapKey);
        return bitSet.get(userId);
    }

    /**
     * 获取当前用户今日打卡状态
     */
    @Override
    public java.util.Map<String, Boolean> getTodayClockStatus(String clockDate) {
        Long userId = LoginUserInfoHelper.getUserId();
        java.util.Map<String, Boolean> result = new java.util.HashMap<>();
        result.put("morningClocked", isAlreadyClocked(userId, clockDate, ClockType.MORNING.getCode()));
        result.put("eveningClocked", isAlreadyClocked(userId, clockDate, ClockType.EVENING.getCode()));
        return result;
    }

    /**
     * 获取当前用户最近三天打卡记录
     */
    @Override
    public List<SysAttendanceRecord> getLatestRecord() {
        Long userId = LoginUserInfoHelper.getUserId();
        
        // 计算日期范围：今天、昨天、前天
        LocalDate today = LocalDate.now();
        LocalDate twoDaysAgo = today.minusDays(2);
        String todayStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String twoDaysAgoStr = twoDaysAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysAttendanceRecord> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(SysAttendanceRecord::getUserId, userId)
                    .ge(SysAttendanceRecord::getClockDate, twoDaysAgoStr)
                    .le(SysAttendanceRecord::getClockDate, todayStr)
                    .orderByDesc(SysAttendanceRecord::getClockTime);
        return this.list(queryWrapper);
    }

    /**
     * 构建Bitmap key
     * @param clockDate 打卡日期 (yyyy-MM-dd)
     * @param clockType 打卡类型
     * @return Bitmap key
     */
    private String buildBitmapKey(String clockDate, String clockType) {
        String dateOnly = clockDate.replace("-", "");
        return AttendanceConfig.ATTENDANCE_BITMAP_KEY_PREFIX + dateOnly + "_" + clockType;
    }
}
