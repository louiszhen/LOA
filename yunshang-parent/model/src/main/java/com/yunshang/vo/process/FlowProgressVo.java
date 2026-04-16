package com.yunshang.vo.process;

import lombok.Data;

import java.util.List;

/**
 * 审批流程进度 VO，用于前端渲染流程图高亮状态
 */
@Data
public class FlowProgressVo {

    /**
     * BPMN XML 字符串，前端使用 bpmn-js 渲染流程图
     */
    private String bpmnXml;

    /**
     * 已完成节点的 BPMN elementId 列表（前端染绿色）
     */
    private List<String> completedActivityIds;

    /**
     * 当前进行中节点的 BPMN elementId 列表（前端染红色）
     */
    private List<String> activeActivityIds;

    /**
     * 流程整体状态（1:审批中 2:审批通过 -1:驳回）
     */
    private Integer processStatus;
}
