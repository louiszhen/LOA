package com.yunshang.model.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yunshang.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @author nanfeng
 * @description 岗位
 * @date 2023-03-05 11:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_post")
public class SysPost extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 岗位编码
     */
    @TableField("post_code")
    private String postCode;

    /**
     * 岗位名称
     */
    @TableField("name")
    private String name;

    /**
     * 显示顺序
     */
    @TableField("description")
    private String description;

    /**
     * 状态（1正常 0停用）
     */
    @TableField("status")
    private Integer status;

}
