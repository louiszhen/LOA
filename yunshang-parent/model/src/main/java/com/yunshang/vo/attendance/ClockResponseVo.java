package com.yunshang.vo.attendance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 打卡响应VO
 * @author louis
 * @date 2026-03-20
 */
@Data
@ApiModel(description = "打卡响应VO")
public class ClockResponseVo {

    @ApiModelProperty(value = "是否成功")
    private Boolean success;

    @ApiModelProperty(value = "打卡状态: normal-正常, late-迟到, early_leave-早退")
    private String status;

    @ApiModelProperty(value = "状态描述")
    private String statusDesc;

    @ApiModelProperty(value = "打卡时间")
    private Date clockTime;

    @ApiModelProperty(value = "打卡类型: morning-上班, evening-下班")
    private String clockType;

    @ApiModelProperty(value = "消息")
    private String message;

    @ApiModelProperty(value = "是否已打卡")
    private Boolean alreadyClocked;
}
