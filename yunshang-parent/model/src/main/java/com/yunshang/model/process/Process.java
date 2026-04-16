package com.yunshang.model.process;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yunshang.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-05 11:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("oa_process")
public class Process extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("process_code")
    private String processCode;

    @TableField("user_id")
    private Long userId;

    @TableField("process_template_id")
    private Long processTemplateId;

    @TableField("process_type_id")
    private Long processTypeId;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("form_values")
    private String formValues;

    @TableField("process_instance_id")
    private String processInstanceId;

    @TableField("current_auditor")
    private String currentAuditor;

    @TableField("status")
    private Integer status;
}
