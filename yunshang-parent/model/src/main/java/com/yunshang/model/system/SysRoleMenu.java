package com.yunshang.model.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yunshang.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @author nanfeng
 * @description 角色菜单
 * @date 2023-03-05 11:55
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_role_menu")
public class SysRoleMenu extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("role_id")
    private Long roleId;

    @TableField("menu_id")
    private Long menuId;

}
