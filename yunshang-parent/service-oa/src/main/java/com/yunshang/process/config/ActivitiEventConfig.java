package com.yunshang.process.config;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description Activiti流程引擎监听器配置
 *              用于注册全局事件监听器
 * @author 
 * @date 2026-03-24
 */
@Slf4j
@Configuration
public class ActivitiEventConfig {

    /**
     * 应用启动完成后注册全局监听器
     */
    @Component
    public static class ActivitiListenerBootstrap implements ApplicationListener<ApplicationReadyEvent> {

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            try {
                // 等待流程引擎初始化
                Thread.sleep(500);
                
                org.activiti.engine.ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
                if (processEngine == null) {
                    log.warn("流程引擎未初始化，跳过监听器注册");
                    return;
                }

                log.info("Activiti流程引擎初始化完成");
                log.info("========================================");
                log.info("TaskListener 监听器配置说明：");
                log.info("需要在 BPMN 文件中为 userTask 添加监听器配置：");
                log.info("");
                log.info("示例配置（使用 Spring Bean 引用）：");
                log.info("<userTask id=\"approval\" name=\"审批\">");
                log.info("  <extensionElements>");
                log.info("    <activiti:taskListener event=\"create\" delegateExpression=\"$taskCreateListener\"/>");
                log.info("    <activiti:taskListener event=\"complete\" delegateExpression=\"$taskCompleteListener\"/>");
                log.info("  </extensionElements>");
                log.info("</userTask>");
                log.info("");
                log.info("或者使用完整类名：");
                log.info("<activiti:taskListener event=\"create\" class=\"com.yunshang.process.listener.TaskCreateListener\"/>");
                log.info("<activiti:taskListener event=\"complete\" class=\"com.yunshang.process.listener.TaskCompleteListener\"/>");
                log.info("========================================");

            } catch (Exception e) {
                log.error("Activiti监听器配置初始化时发生异常: {}", e.getMessage(), e);
            }
        }
    }
}
