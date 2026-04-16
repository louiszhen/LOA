package com.yunshang.model.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("bnt_meeting")
public class SysMeeting {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("meeting_name")
    private String meetingName;

    @TableField("meeting_room_id")
    private Long meetingRoomId;

    @TableField("meeting_resume")
    private String meetingResume;

    @TableField("start")
    private Long start;

    @TableField("end")
    private Long end;

    @TableField("user_id")
    private Long userId;

    /**
     * 是否已过期：0-未过期，1-已过期
     */
    @TableField("is_overtime")
    private Integer isOvertime;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}