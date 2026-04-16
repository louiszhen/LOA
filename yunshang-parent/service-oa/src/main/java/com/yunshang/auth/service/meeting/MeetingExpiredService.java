package com.yunshang.auth.service.meeting;

import com.yunshang.auth.config.MeetingExpiredConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

/**
 * 会议过期服务
 * 使用Redis ZSet实现延迟过期
 *
 * @author louis
 * @date 2026-03-26
 */
@Service
public class MeetingExpiredService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 添加会议过期时间到ZSet
     *
     * @param meetingId 会议ID
     * @param endTime   会议结束时间（时间戳，毫秒）
     */
    public void addMeetingExpired(Long meetingId, Long endTime) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        // 会议ID作为成员，结束时间作为分数
        zSetOps.add(MeetingExpiredConfig.MEETING_EXPIRED_ZSET_KEY, meetingId.toString(), endTime);
    }

    /**
     * 更新会议过期时间到ZSet
     * 当会议结束时间被修改时调用
     *
     * @param meetingId 会议ID
     * @param endTime   新的会议结束时间（时间戳，毫秒）
     */
    public void updateMeetingExpired(Long meetingId, Long endTime) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        // 先删除旧的，再添加新的
        zSetOps.remove(MeetingExpiredConfig.MEETING_EXPIRED_ZSET_KEY, meetingId.toString());
        zSetOps.add(MeetingExpiredConfig.MEETING_EXPIRED_ZSET_KEY, meetingId.toString(), endTime);
    }

    /**
     * 移除会议过期时间（删除会议时调用）
     *
     * @param meetingId 会议ID
     */
    public void removeMeetingExpired(Long meetingId) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        zSetOps.remove(MeetingExpiredConfig.MEETING_EXPIRED_ZSET_KEY, meetingId.toString());
    }

    /**
     * 获取已过期的会议ID列表
     * score <= 当前时间的会议都视为过期
     *
     * @param limit 最多返回数量
     * @return 过期的会议ID列表
     */
    public java.util.List<Long> getExpiredMeetingIds(long limit) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        // 注意：ZSet 中存储的是秒级时间戳，需要将当前时间转换为秒级进行比较
        long currentTimeSeconds = System.currentTimeMillis() / 1000;

        // 分数范围：负无穷到当前时间（秒级）
        java.util.Set<Object> expiredMembers = zSetOps.rangeByScore(
                MeetingExpiredConfig.MEETING_EXPIRED_ZSET_KEY,
                Double.NEGATIVE_INFINITY,
                currentTimeSeconds,
                0,
                limit
        );

        if (expiredMembers == null || expiredMembers.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        return expiredMembers.stream()
                .map(obj -> Long.parseLong(obj.toString()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 删除已处理的过期会议记录
     *
     * @param meetingId 会议ID
     */
    public void removeExpiredMeeting(Long meetingId) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        zSetOps.remove(MeetingExpiredConfig.MEETING_EXPIRED_ZSET_KEY, meetingId.toString());
    }
}
