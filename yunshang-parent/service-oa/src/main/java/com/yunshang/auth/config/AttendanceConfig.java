package com.yunshang.auth.config;

import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;

/**
 * 考勤配置类
 * @author louis
 * @date 2026-03-20
 */
@Configuration
public class AttendanceConfig {

    /**
     * 上班时间 (默认09:00)
     */
    public static final LocalTime MORNING_WORK_TIME = LocalTime.of(9, 0);

    /**
     * 下班时间 (默认18:00)
     */
    public static final LocalTime EVENING_WORK_TIME = LocalTime.of(18, 0);

    /**
     * 迟到容忍时间 (默认09:30，超过30分钟算迟到)
     */
    public static final LocalTime LATE_TOLERANCE_TIME = LocalTime.of(9, 30);

    /**
     * 早退容忍时间 (默认17:30，提前30分钟下班算早退)
     */
    public static final LocalTime EARLY_LEAVE_TOLERANCE_TIME = LocalTime.of(17, 30);

    /**
     * Redis Bitmap key前缀
     */
    public static final String ATTENDANCE_BITMAP_KEY_PREFIX = "attendance:bitmap:";

    /**
     * 考勤队列名称
     */
    public static final String ATTENDANCE_QUEUE_NAME = "attendance:queue";

    /**
     * Redis bitmap过期天数
     */
    public static final int BITMAP_EXPIRE_DAYS = 30;

    /**
     * 考勤统计缓存 key 前缀
     * 完整格式：attendance:statistics:{userId}:{statMonth}
     */
    public static final String STATISTICS_CACHE_KEY_PREFIX = "attendance:statistics:";

    /**
     * 考勤统计缓存过期时间（秒）：1天
     * 22:00 定时任务落库后会主动删除缓存，TTL 仅作兜底保障
     */
    public static final long STATISTICS_CACHE_TTL_SECONDS = 86400L;
}
