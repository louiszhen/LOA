package com.yunshang.vo.attendance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 打卡请求VO
 * @author louis
 * @date 2026-03-20
 */
@Data
@ApiModel(description = "打卡请求VO")
public class ClockRequestVo {

    @ApiModelProperty(value = "打卡类型: morning-上班, evening-下班", required = true)
    private String clockType;

    @ApiModelProperty(value = "打卡时间(格式: yyyy-MM-dd HH:mm:ss), 不传则使用当前时间")
    private Date clockTime;
}
