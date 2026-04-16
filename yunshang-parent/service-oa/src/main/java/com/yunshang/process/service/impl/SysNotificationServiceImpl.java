package com.yunshang.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunshang.model.system.SysNotification;
import com.yunshang.process.mapper.SysNotificationMapper;
import com.yunshang.process.service.SysNotificationService;
import com.yunshang.security.custom.LoginUserInfoHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @description 通知Service实现
 * @author 
 * @date 2026-03-24
 */
@Slf4j
@Service
public class SysNotificationServiceImpl extends ServiceImpl<SysNotificationMapper, SysNotification> 
        implements SysNotificationService {

    /** 通知类型常量 */
    public static final int TYPE_TASK_PENDING = 1;    // 待审核任务通知
    public static final int TYPE_PROCESS_COMPLETE = 2; // 流程完成通知
    public static final int TYPE_PROCESS_REJECT = 3;   // 流程驳回通知

    /** 通知状态常量 */
    public static final int STATUS_UNREAD = 0;    // 未读
    public static final int STATUS_READ = 1;      // 已读
    public static final int STATUS_PROCESSED = 2; // 已处理

    @Resource
    private SysNotificationMapper sysNotificationMapper;

    @Override
    public IPage<SysNotification> findUnprocessedPage(Page<SysNotification> pageParam, Long userId, Integer status) {
        LambdaQueryWrapper<SysNotification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysNotification::getUserId, userId);
        
        if (status != null) {
            queryWrapper.eq(SysNotification::getStatus, status);
        }
        
        queryWrapper.orderByDesc(SysNotification::getCreateTime);
        
        IPage<SysNotification> page = sysNotificationMapper.selectPage(pageParam, queryWrapper);
        return page;
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        SysNotification notification = sysNotificationMapper.selectById(notificationId);
        if (notification == null) {
            log.warn("通知不存在，notificationId: {}", notificationId);
            return;
        }
        
        // 权限校验：只能修改自己的通知
        if (!notification.getUserId().equals(userId)) {
            log.warn("无权修改通知，notificationId: {}, userId: {}", notificationId, userId);
            return;
        }
        
        // 如果已经是已读或已处理状态，不再更新
        if (notification.getStatus() != null && notification.getStatus() >= STATUS_READ) {
            return;
        }
        
        notification.setStatus(STATUS_READ);
        sysNotificationMapper.updateById(notification);
        log.info("通知已标记为已读，notificationId: {}", notificationId);
    }

    @Override
    public void markAsProcessed(Long notificationId, Long userId) {
        SysNotification notification = sysNotificationMapper.selectById(notificationId);
        if (notification == null) {
            log.warn("通知不存在，notificationId: {}", notificationId);
            return;
        }
        
        // 权限校验：只能修改自己的通知
        if (!notification.getUserId().equals(userId)) {
            log.warn("无权修改通知，notificationId: {}, userId: {}", notificationId, userId);
            return;
        }
        
        notification.setStatus(STATUS_PROCESSED);
        sysNotificationMapper.updateById(notification);
        log.info("通知已标记为已处理，notificationId: {}", notificationId);
    }

    @Override
    public SysNotification createTaskNotification(Long processId, String taskId, Long userId,
                                                   String title, String content, String extraData) {
        SysNotification notification = new SysNotification();
        notification.setType(TYPE_TASK_PENDING);
        notification.setProcessId(processId);
        notification.setTaskId(taskId);
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setStatus(STATUS_UNREAD);
        notification.setExtraData(extraData);
        
        sysNotificationMapper.insert(notification);
        log.info("创建任务通知成功，notificationId: {}, userId: {}, processId: {}", 
                notification.getId(), userId, processId);
        
        return notification;
    }

    @Override
    public SysNotification createProcessEndNotification(Long processId, Long userId,
                                                         String title, String content, String extraData) {
        SysNotification notification = new SysNotification();
        notification.setType(TYPE_PROCESS_COMPLETE);
        notification.setProcessId(processId);
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setStatus(STATUS_UNREAD);
        notification.setExtraData(extraData);
        
        sysNotificationMapper.insert(notification);
        log.info("创建流程结束通知成功，notificationId: {}, userId: {}, processId: {}", 
                notification.getId(), userId, processId);
        
        return notification;
    }

    @Override
    public Long getUnprocessedCount(Long userId) {
        LambdaQueryWrapper<SysNotification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysNotification::getUserId, userId);
        queryWrapper.eq(SysNotification::getStatus, STATUS_UNREAD);
        
        return sysNotificationMapper.selectCount(queryWrapper);
    }
}
