package com.yunshang.model.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("bnt_meeting_room")
public class SysMeetingRoom{
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("room_number")
    private String roomNumber;

    @TableField("is_available")
    private Boolean isAvailable;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
