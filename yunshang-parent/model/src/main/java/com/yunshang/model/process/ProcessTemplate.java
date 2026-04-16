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
 * @date 2023-03-05 11:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("oa_process_template")
public class ProcessTemplate extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板名称
     */
    @TableField("name")
    private String name;

    /**
     * 图标路径
     */
    @TableField("icon_url")
    private String iconUrl;

    /**
     * processTypeId
     */
    @TableField("process_type_id")
    private Long processTypeId;

    /**
     * 表单属性
     */
    @TableField("form_props")
    private String formProps;

    /**
     * 表单选项
     */
    @TableField("form_options")
    private String formOptions;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 流程定义key
     */
    @TableField("process_definition_key")
    private String processDefinitionKey;

    /**
     * 流程定义上传路径
     */
    @TableField("process_definition_path")
    private String processDefinitionPath;

    /**
     * 流程定义模型id
     */
    @TableField("process_model_id")
    private String processModelId;

    /**
     * 状态
     */
    @TableField("status")
    private Integer status;

    @TableField(exist = false)
    private String processTypeName;
}
