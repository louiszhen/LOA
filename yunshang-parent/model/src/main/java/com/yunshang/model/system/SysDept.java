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
 * @description 部门
 * @date 2023-03-05 11:44
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 部门名称
     */
    @TableField("name")
    private String name;

    /**
     * 上级部门id
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 树结构
     */
    @TableField("tree_path")
    private String treePath;

    /**
     * 排序
     */
    @TableField("sort_value")
    private Integer sortValue;

    /**
     * 负责人
     */
    @TableField("leader")
    private String leader;

    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 状态（1正常 0停用）
     */
    @TableField("status")
    private Integer status;

    /**
     * 部门简介
     */
    @TableField("description")
    private String description;

    /**
     * 下级部门
     */
    @TableField(exist = false)
    private List<SysDept> children;

}
