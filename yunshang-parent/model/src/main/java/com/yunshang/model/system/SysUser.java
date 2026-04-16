package com.yunshang.model.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yunshang.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-05 11:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_user")
public class SysUser extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("name")
    private String name;

    @TableField("phone")
    private String phone;

    @TableField("head_url")
    private String headUrl;

    @TableField("dept_id")
    private Long deptId;

    @TableField("post_id")
    private Long postId;

    @TableField("description")
    private String description;

    @TableField("open_id")
    private String openId;

    /**
     * 状态（1：正常 0：停用）
     */
    @TableField("status")
    private Integer status;

    @TableField(exist = false)
    private List<SysRole> roleList;

    /**
     * 岗位
     */
    @TableField(exist = false)
    private String postName;

    /**
     * 部门
     */
    @TableField(exist = false)
    private String deptName;
}
