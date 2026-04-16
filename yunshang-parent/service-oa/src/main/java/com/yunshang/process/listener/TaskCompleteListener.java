package com.yunshang.process.listener;

import com.alibaba.fastjson2.JSON;
import com.yunshang.auth.service.SysUserService;
import com.yunshang.model.process.Process;
import com.yunshang.model.system.SysNotification;
import com.yunshang.model.system.SysUser;
import com.yunshang.process.service.ProcessService;
import com.yunshang.process.service.SseEmitterService;
import com.yunshang.process.service.SysNotificationService;
import com.yunshang.process.service.impl.SysNotificationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.Task;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @description Activiti任务完成监听器
 *              当任务完成时触发，用于通知申请人流程结果
 * @author 
 * @date 2026-03-24
 */
@Slf4j
@Component
public class TaskCompleteListener implements TaskListener {

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
        log.info("任务完成事件触发，taskId: {}, taskName: {}", 
                delegateTask.getId(), delegateTask.getName());

        try {
            // 获取任务信息
            String taskId = delegateTask.getId();
            String taskName = delegateTask.getName();
            String processInstanceId = delegateTask.getProcessInstanceId();

            // 获取流程业务KEY
            String businessKey = null;
            if (delegateTask.getExecution() != null) {
                Object businessKeyObj = delegateTask.getExecution().getVariable("businessKey");
                if (businessKeyObj != null) {
                    businessKey = businessKeyObj.toString();
                }
            }
            
            if (businessKey == null || businessKey.isEmpty()) {
                log.warn("任务没有关联业务Key，跳过通知，taskId: {}", taskId);
                return;
            }

            Long processId = Long.parseLong(businessKey);

            // 获取流程信息
            Process process = processService.getById(processId);
            if (process == null) {
                log.warn("流程记录不存在，processId: {}", processId);
                return;
            }

            // 检查是否还有下一个任务
            boolean hasNextTask = this.hasNextTask(process.getProcessInstanceId(), taskId);
            
            // 如果没有下一个任务，说明流程结束，通知申请人
            if (!hasNextTask) {
                this.notifyProcessEnd(process, taskId, taskName);
            }

        } catch (Exception e) {
            log.error("处理任务完成事件时发生异常", e);
        }
    }

    /**
     * 检查流程实例是否还有下一个待处理的任务
     */
    private boolean hasNextTask(String processInstanceId, String currentTaskId) {
        try {
            // 查询该流程实例是否还有未完成的任务
            long count = processService.getCurrentTaskCount(processInstanceId);
            return count > 0;
        } catch (Exception e) {
            log.warn("检查下一任务时发生异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 通知申请人流程结束
     */
    private void notifyProcessEnd(Process process, String taskId, String taskName) {
        try {
            if (process.getUserId() == null) {
                log.warn("流程申请人ID为空，跳过通知，processId: {}", process.getId());
                return;
            }

            // 获取申请人信息
            SysUser applicant = sysUserService.getById(process.getUserId());
            if (applicant == null) {
                log.warn("申请人信息不存在，userId: {}", process.getUserId());
                return;
            }

            // 判断流程状态：process.getStatus() 1-审批中 2-已完成 -1-已驳回
            int notificationType;
            String title;
            String content;
            
            if (process.getStatus() != null && process.getStatus() == 2) {
                // 审批完成（通过）
                notificationType = SysNotificationServiceImpl.TYPE_PROCESS_COMPLETE;
                title = "您的申请已审批完成";
                content = String.format("您提交的「%s」申请已审批通过", process.getTitle());
            } else if (process.getStatus() != null && process.getStatus() == -1) {
                // 审批驳回
                notificationType = SysNotificationServiceImpl.TYPE_PROCESS_REJECT;
                title = "您的申请已被驳回";
                content = String.format("您提交的「%s」申请已被驳回", process.getTitle());
            } else {
                // 默认流程结束
                notificationType = SysNotificationServiceImpl.TYPE_PROCESS_COMPLETE;
                title = "您的申请已处理";
                content = String.format("您提交的「%s」申请已处理完成", process.getTitle());
            }

            // 构建扩展数据
            Map<String, Object> extraData = new HashMap<>();
            extraData.put("processCode", process.getProcessCode());
            extraData.put("processTitle", process.getTitle());
            extraData.put("taskName", taskName);
            extraData.put("status", process.getStatus());
            extraData.put("description", process.getDescription());
            String extraDataJson = JSON.toJSONString(extraData);

            // 创建通知记录
            SysNotification notification = new SysNotification();
            notification.setType(notificationType);
            notification.setProcessId(process.getId());
            notification.setTaskId(taskId);
            notification.setUserId(applicant.getId());
            notification.setTitle(title);
            notification.setContent(content);
            notification.setStatus(SysNotificationServiceImpl.STATUS_UNREAD);
            notification.setExtraData(extraDataJson);
            sysNotificationService.save(notification);

            // 通过SSE实时推送给申请人
            Map<String, Object> pushData = new HashMap<>();
            pushData.put("type", "notification");
            pushData.put("notification", notification);
            sseEmitterService.sendNotificationToUser(applicant.getId(), pushData);

            log.info("流程结束通知已发送，notificationId: {}, userId: {}, type: {}", 
                    notification.getId(), applicant.getId(), notificationType);

        } catch (Exception e) {
            log.error("发送流程结束通知时发生异常，processId: {}", process.getId(), e);
        }
    }
}
