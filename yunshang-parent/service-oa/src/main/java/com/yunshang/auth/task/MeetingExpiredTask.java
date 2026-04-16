package com.yunshang.auth.task;

import com.yunshang.auth.mapper.SysMeetingMapper;
import com.yunshang.auth.service.meeting.MeetingExpiredService;
import com.yunshang.model.system.SysMeeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 会议过期定时任务
 * 定期扫描Redis ZSet中的过期会议，标记为已过期
 *
 * @author louis
 * @date 2026-03-26
 */
@Component
public class MeetingExpiredTask {

    private static final Logger logger = LoggerFactory.getLogger(MeetingExpiredTask.class);

    /**
     * 每次处理的最大过期会议数量
     */
    private static final long BATCH_SIZE = 100;

    @Autowired
    private MeetingExpiredService meetingExpiredService;

    @Autowired
    private SysMeetingMapper sysMeetingMapper;

    /**
     * 每分钟执行一次，扫描并处理过期的会议
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processExpiredMeetings() {
        try {
            // 获取已过期的会议ID列表
            List<Long> expiredMeetingIds = meetingExpiredService.getExpiredMeetingIds(BATCH_SIZE);

            if (expiredMeetingIds == null || expiredMeetingIds.isEmpty()) {
                return;
            }

            logger.info("扫描到 {} 个过期会议", expiredMeetingIds.size());

            for (Long meetingId : expiredMeetingIds) {
                try {
                    processExpiredMeeting(meetingId);
                } catch (Exception e) {
                    logger.error("处理过期会议失败: meetingId={}", meetingId, e);
                    // 继续处理下一个
                }
            }
        } catch (Exception e) {
            logger.error("扫描过期会议失败", e);
        }
    }

    /**
     * 处理单个过期会议
     *
     * @param meetingId 会议ID
     */
    private void processExpiredMeeting(Long meetingId) {
        // 查询会议信息（@TableLogic 自动过滤 is_deleted=1，已删除会议返回 null）
        SysMeeting meeting = sysMeetingMapper.selectById(meetingId);

        if (meeting == null) {
            logger.warn("会议不存在或已被删除，从ZSet移除: meetingId={}", meetingId);
            meetingExpiredService.removeExpiredMeeting(meetingId);
            return;
        }

        // 检查会议是否已经被标记为过期
        if (meeting.getIsOvertime() != null && meeting.getIsOvertime() == 1) {
            logger.info("会议已被标记为过期，从ZSet移除: meetingId={}", meetingId);
            meetingExpiredService.removeExpiredMeeting(meetingId);
            return;
        }

        // 回查数据库 end 字段，确认会议确实已结束，防止 ZSet 数据异常导致错误过期
        // 注意：数据库存储的是秒级时间戳，需要将当前时间转换为秒级进行比较
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        if (meeting.getEnd() == null || meeting.getEnd() > currentTimeSeconds) {
            logger.warn("会议尚未结束，跳过过期处理: meetingId={}, end={}, currentTimeSeconds={}",
                    meetingId, meeting.getEnd(), currentTimeSeconds);
            return;
        }

        // 标记会议为已过期
        SysMeeting updateMeeting = new SysMeeting();
        updateMeeting.setId(meetingId);
        updateMeeting.setIsOvertime(1);
        sysMeetingMapper.updateById(updateMeeting);

        // 从ZSet中移除
        meetingExpiredService.removeExpiredMeeting(meetingId);

        logger.info("会议已标记为过期: meetingId={}, meetingName={}",
                meetingId, meeting.getMeetingName());
    }
}

