package com.yunshang.process.listener;

import com.yunshang.auth.service.SysUserService;
import com.yunshang.model.system.SysUser;
import com.yunshang.process.service.ProcessService;
import com.yunshang.process.service.SseEmitterService;
import com.yunshang.vo.process.ProcessVo;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Activiti任务创建监听器 - 待审核列表SSE增量推送
 */
@Slf4j
@Component
public class TaskCreateListener implements TaskListener {

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
        try {
            String taskId = delegateTask.getId();
            String assignee = delegateTask.getAssignee();
            String processInstanceId = delegateTask.getProcessInstanceId();

            // 跳过无处理人任务
            if (assignee == null || assignee.isEmpty()) {
                return;
            }

            // 获取processId
            Long processId = getProcessId(processInstanceId);
            if (processId == null) {
                return;
            }

            // 获取审批人用户ID
            SysUser assigneeUser = sysUserService.getByUsername(assignee);
            if (assigneeUser == null) {
                return;
            }

            // 构建ProcessVo并推送SSE
            ProcessVo processVo = processService.buildProcessVo(processService.getById(processId), taskId);
            if (processVo != null) {
                sseEmitterService.sendNewPendingToUser(assigneeUser.getId(), processVo);
                log.info("待审核SSE推送成功，processId: {}, assigneeUserId: {}", processId, assigneeUser.getId());
            }
        } catch (Exception e) {
            log.error("任务创建SSE推送异常: {}", e.getMessage());
        }
    }

    private Long getProcessId(String processInstanceId) {
        if (processInstanceId == null) {
            return null;
        }
        org.activiti.engine.runtime.ProcessInstance processInstance = 
                processService.getProcessInstanceById(processInstanceId);
        if (processInstance == null || processInstance.getBusinessKey() == null) {
            return null;
        }
        try {
            return Long.parseLong(processInstance.getBusinessKey());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
