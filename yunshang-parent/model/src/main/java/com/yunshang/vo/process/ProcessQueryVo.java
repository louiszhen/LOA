package com.yunshang.vo.process;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class ProcessQueryVo {

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 申请人id
     */
    private Long userId;

    /**
     * 对应的流程模版id
     */
    @TableField("process_template_id")
    private Long processTemplateId;

    /**
     * 对应的流程类型id
     */
    private Long processTypeId;

    private String createTimeBegin;

    private String createTimeEnd;

    /**
     * 状态（0：默认 1：审批中 2：审批通过 -1：驳回）
     */
    private Integer status;
}