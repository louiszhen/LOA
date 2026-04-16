package com.yunshang.model.attendance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 打卡统计表
 * @author louis
 * @date 2026-03-20
 */
@Data
@TableName("sys_attendance_statistics")
public class SysAttendanceStatistics implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户姓名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 统计月份 (格式: yyyyMM, 例如: 202601)
     */
    @TableField("stat_month")
    private String statMonth;

    /**
     * 应打卡天数
     */
    @TableField("should_clock_days")
    private Integer shouldClockDays;

    /**
     * 实际打卡天数
     */
    @TableField("actual_clock_days")
    private Integer actualClockDays;

    /**
     * 正常打卡天数
     */
    @TableField("normal_clock_days")
    private Integer normalClockDays;

    /**
     * 迟到天数
     */
    @TableField("late_days")
    private Integer lateDays;

    /**
     * 早退天数
     */
    @TableField("early_leave_days")
    private Integer earlyLeaveDays;

    /**
     * 未打卡天数
     */
    @TableField("absent_days")
    private Integer absentDays;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 是否删除: 0-未删除, 1-已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}

