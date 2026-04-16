package com.yunshang.vo.process;

import lombok.Data;

@Data
public class ProcessFormVo {

    /**
     * 审批模板id
     */
    private Long processTemplateId;

    /**
     * 审批类型id
     */
    private Long processTypeId;

    /**
     * 表单值
     */
    private String formValues;

}