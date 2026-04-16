package com.yunshang.vo.attendance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 打卡记录查询VO
 * @author louis
 * @date 2026-03-20
 */
@Data
@ApiModel(description = "打卡记录查询VO")
public class AttendanceRecordQueryVo {

    @ApiModelProperty(value = "开始日期(格式: yyyy-MM-dd)")
    private String startDate;

    @ApiModelProperty(value = "结束日期(格式: yyyy-MM-dd)")
    private String endDate;

    @ApiModelProperty(value = "打卡类型: morning-上班, evening-下班")
    private String clockType;

    @ApiModelProperty(value = "打卡状态: normal-正常, late-迟到, early_leave-早退, absent-未打卡")
    private String status;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户姓名")
    private String userName;
}
