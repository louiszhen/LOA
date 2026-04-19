package com.yunshang.process.listener;

import com.alibaba.fastjson2.JSON;
import com.yunshang.auth.service.SysUserService;
import com.yunshang.model.process.Process;
import com.yunshang.model.system.SysNotification;
import com.yunshang.model.system.SysUser;
import com.yunshang.process.service.ProcessService;
import com.yunshang.process.service.SseEmitterService;
import com.yunshang.process.service.SysNotificationService;
import com.yunshang.vo.process.ProcessVo;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @description Activiti任务创建监听器
 *              当新任务被创建时触发，用于发送通知给任务接收人
 * @author 
 * @date 2026-03-24
 */
@Slf4j
@Component
public class TaskCreateListener implements TaskListener {

    @Resource
    @Lazy
    private SysNotificationService sysNotificationService;

    @Resource
    @Lazy
    private SseEmitterService sseEmitterService;

    @Resource
    @Lazy
    private ProcessService processService;

    @Resource
    @Lazy
    private SysUserService sysUserService;

    @Override
    public void notify(DelegateTask delegateTask) {
        log.info("任务创建事件触发，taskId: {}, taskName: {}", 
                delegateTask.getId(), delegateTask.getName());

        try {
            // 获取任务信息
            String taskId = delegateTask.getId();
            String taskName = delegateTask.getName();
            String assignee = delegateTask.getAssignee();
            String processInstanceId = delegateTask.getProcessInstanceId();

            // 跳过开始节点（开始节点通常没有处理人）
            if (taskName == null || taskName.contains("开始") || taskName.contains("Start")) {
                log.debug("跳过开始节点，taskId: {}", taskId);
                return;
            }

            if (assignee == null || assignee.isEmpty()) {
                log.info("任务没有指定处理人，跳过通知，taskId: {}", taskId);
                return;
            }

            // 获取流程业务KEY
            // businessKey存储在ACT_RU_EXECUTION表的BUSINESS_KEY_字段
            // 通过processInstanceId查询获取
            String businessKey = null;
            if (processInstanceId != null) {
                // 通过ProcessService间接获取RuntimeService
                org.activiti.engine.runtime.ProcessInstance processInstance = 
                    processService.getProcessInstanceById(processInstanceId);
                if (processInstance != null && processInstance.getBusinessKey() != null) {
                    businessKey = processInstance.getBusinessKey();
                }
            }
            
            if (businessKey == null || businessKey.isEmpty()) {
                log.warn("任务没有关联业务Key，跳过通知，taskId: {}, processInstanceId: {}", 
                        taskId, processInstanceId);
                return;
            }

            Long processId = Long.parseLong(businessKey);

            // 优先从 Activiti 流程变量读取 process 关键信息（避免查库读到未提交数据）
            // 变量由 startUp() 在 startProcessInstanceByKey 前注入
            Long processIdVar = getVarAsLong(delegateTask, "_processId");
            String processTitle = (String) delegateTask.getVariable("_processTitle");
            String processCode  = (String) delegateTask.getVariable("_processCode");
            Long   processUserId = getVarAsLong(delegateTask, "_processUserId");

            // 兼容旧流程（变量未注入时降级查库）
            if (processTitle == null || processCode == null || processUserId == null) {
                log.warn("流程变量缺失，降级查库，taskId: {}, processId: {}", taskId, processId);
                Process process = processService.getById(processId);
                if (process == null) {
                    log.warn("流程记录不存在，processId: {}", processId);
                    return;
                }
                processTitle  = process.getTitle();
                processCode   = process.getProcessCode();
                processUserId = process.getUserId();
            }
            if (processIdVar != null) {
                processId = processIdVar;
            }

            // 获取申请人信息（sys_user 是独立表，无事务可见性问题）
            SysUser applicant = sysUserService.getById(processUserId);
            String applicantName = applicant != null ? applicant.getName() : "未知";

            // 获取处理人信息
            SysUser assigneeUser = sysUserService.getByUsername(assignee);
            Long assigneeUserId = assigneeUser != null ? assigneeUser.getId() : null;

            if (assigneeUserId == null) {
                log.warn("处理人用户不存在，username: {}", assignee);
                return;
            }

            // 构建通知内容
            String title = "您有一个新的审批任务";
            String content = String.format("%s 提交了 %s 申请，请及时处理",
                    applicantName, processTitle);

            // 构建扩展数据
            Map<String, Object> extraData = new HashMap<>();
            extraData.put("processCode", processCode);
            extraData.put("processTitle", processTitle);
            extraData.put("taskName", taskName);
            extraData.put("applicantName", applicantName);
            extraData.put("applicantId", processUserId);
            String extraDataJson = JSON.toJSONString(extraData);

            // 创建通知记录
            SysNotification notification = sysNotificationService.createTaskNotification(
                    processId,
                    taskId,
                    assigneeUserId,
                    title,
                    content,
                    extraDataJson
            );

            // 通过SSE实时推送给用户（通知消息）
            Map<String, Object> pushData = new HashMap<>();
            pushData.put("type", "notification");
            pushData.put("notification", notification);
            sseEmitterService.sendNotificationToUser(assigneeUserId, pushData);

            // 通过SSE推送完整的ProcessVo（与findPending接口返回字段一致，供待审核列表增量更新使用）
            ProcessVo processVo = processService.buildProcessVo(
                    processService.getById(processId), taskId);
            if (processVo != null) {
                sseEmitterService.sendNewPendingToUser(assigneeUserId, processVo);
                log.info("待审核ProcessVo已推送，processId: {}, assigneeUserId: {}", processId, assigneeUserId);
            }

            log.info("任务创建通知已发送，notificationId: {}, assigneeUserId: {}", 
                    notification.getId(), assigneeUserId);

        } catch (Exception e) {
            log.error("处理任务创建事件时发生异常", e);
        }
    }

    /**
     * 将 Activiti 流程变量安全转为 Long（兼容 Integer/Long 两种存储类型）
     */
    private Long getVarAsLong(DelegateTask task, String varName) {
        Object val = task.getVariable(varName);
        if (val instanceof Long) return (Long) val;
        if (val instanceof Integer) return ((Integer) val).longValue();
        if (val instanceof String) {
            try { return Long.parseLong((String) val); } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}
