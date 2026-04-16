package com.yunshang.vo.process;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class ProcessVo {

    private Long id;

    private Date createTime;

    private String processCode;

    private Long userId;

    /**
     * 申请人姓名
     */
    private String name;

    @TableField("process_template_id")
    private Long processTemplateId;

    private String processTemplateName;

    private Long processTypeId;

    private String processTypeName;

    private String title;

    private String description;

    /**
     * 表单属性
     */
    private String formProps;

    /**
     * 表单选项
     */
    private String formOptions;

    /**
     * 表单属性值
     */
    private String formValues;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 当前审批人
     */
    private String currentAuditor;

    /**
     * 状态（0：默认 1：审批中 2：审批通过 -1：驳回）
     */
    private Integer status;

    private String taskId;
}