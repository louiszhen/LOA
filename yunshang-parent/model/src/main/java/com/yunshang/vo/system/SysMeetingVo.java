package com.yunshang.vo.system;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 会议VO，包含会议室号
 */
@Data
public class SysMeetingVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String meetingName;

    private Long meetingRoomId;

    /**
     * 会议室号
     */
    private String roomNumber;

    private String meetingResume;

    private Long start;

    private Long end;

    private Long userId;

    /**
     * 是否已过期：0-未过期，1-已过期
     */
    private Integer isOvertime;
}
