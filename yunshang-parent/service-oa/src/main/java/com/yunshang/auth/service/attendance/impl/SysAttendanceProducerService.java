package com.yunshang.auth.service.attendance.impl;

import com.yunshang.vo.attendance.AttendanceMessageVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 考勤消息生产者服务
 * @author louis
 * @date 2026-03-20
 */
@Slf4j
@Service
public class SysAttendanceProducerService {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 队列名称
     */
    private static final String ATTENDANCE_QUEUE_NAME = "attendance:queue";

    /**
     * 发送考勤消息到队列
     * @param message 考勤消息
     */
    public void sendAttendanceMessage(AttendanceMessageVo message) {
        try {
            RBlockingQueue<AttendanceMessageVo> queue = redissonClient.getBlockingQueue(ATTENDANCE_QUEUE_NAME);
            queue.offer(message);
            log.info("考勤消息已发送到队列: userId={}, clockDate={}, clockType={}", 
                message.getUserId(), message.getClockDate(), message.getClockType());
        } catch (Exception e) {
            log.error("发送考勤消息失败", e);
            throw new RuntimeException("发送考勤消息失败", e);
        }
    }
}
