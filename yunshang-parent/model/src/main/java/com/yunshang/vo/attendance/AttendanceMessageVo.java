package com.yunshang.vo.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 打卡消息VO，用于队列消息传递
 * @author louis
 * @date 2026-03-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceMessageVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 打卡日期 (格式: yyyy-MM-dd)
     */
    private String clockDate;

    /**
     * 打卡月份 (格式: yyyyMM)
     */
    private String clockMonth;

    /**
     * 打卡类型: morning-上班, evening-下班
     */
    private String clockType;

    /**
     * 打卡时间
     */
    private Date clockTime;

    /**
     * 打卡状态: normal-正常, late-迟到, early_leave-早退
     */
    private String status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 备注
     */
    private String remark;
}
