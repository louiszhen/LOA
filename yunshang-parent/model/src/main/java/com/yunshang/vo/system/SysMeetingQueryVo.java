package com.yunshang.vo.system;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 会议查询实体
 */
@Data
public class SysMeetingQueryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会议名称（模糊查询）
     */
    private String meetingName;

    /**
     * 会议室ID
     */
    private Long meetingRoomId;

    /**
     * 开始时间范围-开始时间（时间戳）
     */
    private Long startTime;

    /**
     * 开始时间范围-结束时间（时间戳）
     */
    private Long endTime;
}
