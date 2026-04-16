package com.yunshang.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yunshang.model.process.Process;
import com.yunshang.vo.process.ApprovalVo;
import com.yunshang.vo.process.ProcessFormVo;
import com.yunshang.vo.process.ProcessQueryVo;
import com.yunshang.vo.process.FlowProgressVo;
import com.yunshang.vo.process.ProcessVo;
//import me.chanjar.weixin.common.error.WxErrorException;

import java.util.Map;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-18 17:20
 */
public interface ProcessService extends IService<Process> {

    /**
     * 根据条件分页查询流程实例
     */
    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);

    /**
     * 根据上传的zip文件部署流程定义
     * @return 实际部署的流程定义key（来自BPMN文件中process标签的id属性）
     */
    String deployByZip(String deployPath);

    /**
     * 启动流程审批实例
     */
    void startUp(ProcessFormVo processFormVo);

    /**
     * 查询待处理任务
     */
    IPage<ProcessVo> findPending(Page<Process> pageParam);

    /**
     * 获取审批详情
     */
    Map<String, Object> show(Long id);

    /**
     * 审批操作
     */
    void approve(ApprovalVo approvalVo);

    /**
     * 查询已处理接口
     */
    IPage<ProcessVo> findProcessed(Page<Process> pageParam);

    /**
     * 查询已发起申请
     */
    IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam);

    /**
     * 获取流程实例当前未完成任务数量
     *
     * @param processInstanceId 流程实例ID
     * @return 未完成任务数量
     */
    long getCurrentTaskCount(String processInstanceId);

    /**
     * 获取流程审批进度（BPMN XML + 各状态节点 ID 列表）
     *
     * @param processId oa_process 业务主键
     * @return FlowProgressVo
     */
    FlowProgressVo getFlowProgress(Long processId);

    /**
     * 根据流程实例ID获取流程实例
     *
     * @param processInstanceId 流程实例ID
     * @return 流程实例对象，如果不存在返回null
     */
    org.activiti.engine.runtime.ProcessInstance getProcessInstanceById(String processInstanceId);
}
