package com.yunshang.auth.config;

/**
 * 会议过期Redis配置常量类
 * 使用ZSet存储会议过期时间，score为过期时间戳
 *
 * @author louis
 * @date 2026-03-26
 */
public class MeetingExpiredConfig {

    /**
     * 会议过期ZSet的Key
     * 成员：会议ID
     * 分数：会议结束时间戳（毫秒）
     */
    public static final String MEETING_EXPIRED_ZSET_KEY = "meeting:expired:zset";
}
