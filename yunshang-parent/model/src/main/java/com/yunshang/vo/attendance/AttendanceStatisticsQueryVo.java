package com.yunshang.vo.attendance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 考勤统计查询VO
 * @author louis
 * @date 2026-03-20
 */
@Data
@ApiModel(description = "考勤统计查询VO")
public class AttendanceStatisticsQueryVo {

    @ApiModelProperty(value = "统计月份(格式: yyyyMM, 例如: 202601)")
    private String statMonth;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户姓名")
    private String userName;
}
