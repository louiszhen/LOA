package com.yunshang.model.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yunshang.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Date;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-05 11:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_login_log")
public class SysLoginLog extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    @TableField("username")
    private String username;

    /**
     * 登录IP地址
     */
    @TableField("ipaddr")
    private String ipaddr;

    /**
     * 登录状态（0成功 1失败）
     */
    @TableField("status")
    private Integer status;

    /**
     * 提示信息
     */
    @TableField("msg")
    private String msg;

    /**
     * 访问时间
     */
    @TableField("access_time")
    private Date accessTime;

}
