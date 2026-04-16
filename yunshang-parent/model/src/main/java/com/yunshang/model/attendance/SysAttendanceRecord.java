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
 * 打卡明细表
 * @author louis
 * @date 2026-03-20
 */
@Data
@TableName("sys_attendance_record")
public class SysAttendanceRecord implements Serializable {

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
     * 打卡日期 (格式: yyyy-MM-dd)
     */
    @TableField("clock_date")
    private String clockDate;

    /**
     * 打卡类型: morning-上班, evening-下班
     */
    @TableField("clock_type")
    private String clockType;

    /**
     * 打卡时间
     */
    @TableField("clock_time")
    private Date clockTime;

    /**
     * 打卡状态: normal-正常, late-迟到, early_leave-早退, absent-未打卡
     */
    @TableField("status")
    private String status;

    /**
     * 打卡状态描述
     */
    @TableField("status_desc")
    private String statusDesc;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

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

