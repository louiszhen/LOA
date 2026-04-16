package com.yunshang.model.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yunshang.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @author nanfeng
 * @description 用户角色
 * @date 2023-03-05 12:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_user_role")
public class SysUserRole extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("role_id")
    private Long roleId;

    @TableField("user_id")
    private Long userId;
}
