package com.yunshang.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yunshang.model.system.SysNotification;

/**
 * @description 通知Service
 * @author 
 * @date 2026-03-24
 */
public interface SysNotificationService extends IService<SysNotification> {

    /**
     * 分页查询用户未处理的通知
     *
     * @param pageParam 分页参数
     * @param userId    用户ID
     * @param status    通知状态（可选，null表示所有）
     * @return 分页结果
     */
    IPage<SysNotification> findUnprocessedPage(Page<SysNotification> pageParam, Long userId, Integer status);

    /**
     * 标记通知为已读
     *
     * @param notificationId 通知ID
     * @param userId         用户ID（用于权限校验）
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 标记通知为已处理
     *
     * @param notificationId 通知ID
     * @param userId         用户ID（用于权限校验）
     */
    void markAsProcessed(Long notificationId, Long userId);

    /**
     * 创建任务通知
     *
     * @param processId   流程ID
     * @param taskId       任务ID
     * @param userId       接收人用户ID
     * @param title        通知标题
     * @param content      通知内容
     * @param extraData    扩展数据JSON
     * @return 创建的通知
     */
    SysNotification createTaskNotification(Long processId, String taskId, Long userId, 
                                           String title, String content, String extraData);

    /**
     * 创建流程结束通知
     *
     * @param processId   流程ID
     * @param userId       接收人用户ID
     * @param title        通知标题
     * @param content      通知内容
     * @param extraData    扩展数据JSON
     * @return 创建的通知
     */
    SysNotification createProcessEndNotification(Long processId, Long userId, 
                                                  String title, String content, String extraData);

    /**
     * 获取用户未处理通知数量
     *
     * @param userId 用户ID
     * @return 未处理通知数量
     */
    Long getUnprocessedCount(Long userId);
}
