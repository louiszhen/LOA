package com.yunshang.process.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunshang.common.result.Result;
import com.yunshang.model.system.SysNotification;
import com.yunshang.process.service.SseEmitterService;
import com.yunshang.process.service.SysNotificationService;
import com.yunshang.security.custom.LoginUserInfoHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @description 通知Controller
 * @author 
 * @date 2026-03-24
 */
@Slf4j
@RestController
@RequestMapping("/admin/notification")
public class SysNotificationController {

    @Resource
    private SysNotificationService sysNotificationService;

    @Resource
    private SseEmitterService sseEmitterService;

    /**
     * 建立SSE连接
     * 前端登录后调用此接口建立SSE长连接，用于接收实时通知
     */
    @GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        Long userId = LoginUserInfoHelper.getUserId();
        log.info("用户建立SSE连接，userId: {}", userId);
        return sseEmitterService.connect(userId);
    }

    /**
     * 断开SSE连接
     * 前端退出登录时调用此接口断开SSE连接
     */
    @PostMapping("/sse/disconnect")
    public Result<Void> disconnect() {
        Long userId = LoginUserInfoHelper.getUserId();
        log.info("用户断开SSE连接，userId: {}", userId);
        sseEmitterService.disconnect(userId);
        return Result.ok();
    }

    /**
     * 查询未处理的通知列表（分页）
     */
    @GetMapping("/list/unprocessed")
    public Result<IPage<SysNotification>> getUnprocessedList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status) {
        
        Long userId = LoginUserInfoHelper.getUserId();
        Page<SysNotification> page = new Page<>(current, size);
        IPage<SysNotification> result = sysNotificationService.findUnprocessedPage(page, userId, status);
        return Result.ok(result);
    }

    /**
     * 获取未处理通知数量
     */
    @GetMapping("/count/unprocessed")
    public Result<Long> getUnprocessedCount() {
        Long userId = LoginUserInfoHelper.getUserId();
        Long count = sysNotificationService.getUnprocessedCount(userId);
        return Result.ok(count);
    }

    /**
     * 标记通知为已读
     */
    @PostMapping("/mark/read/{notificationId}")
    public Result<Void> markAsRead(@PathVariable Long notificationId) {
        Long userId = LoginUserInfoHelper.getUserId();
        sysNotificationService.markAsRead(notificationId, userId);
        return Result.ok();
    }

    /**
     * 标记通知为已处理
     */
    @PostMapping("/mark/processed/{notificationId}")
    public Result<Void> markAsProcessed(@PathVariable Long notificationId) {
        Long userId = LoginUserInfoHelper.getUserId();
        sysNotificationService.markAsProcessed(notificationId, userId);
        return Result.ok();
    }

    /**
     * 批量标记通知为已读
     */
    @PostMapping("/mark/read/batch")
    public Result<Void> batchMarkAsRead(@RequestBody Long[] notificationIds) {
        Long userId = LoginUserInfoHelper.getUserId();
        for (Long notificationId : notificationIds) {
            sysNotificationService.markAsRead(notificationId, userId);
        }
        return Result.ok();
    }

    /**
     * 获取通知详情
     */
    @GetMapping("/detail/{notificationId}")
    public Result<?> getDetail(@PathVariable Long notificationId) {
        SysNotification notification = sysNotificationService.getById(notificationId);
        if (notification == null) {
            return Result.fail("通知不存在");
        }
        
        // 权限校验
        Long userId = LoginUserInfoHelper.getUserId();
        if (!notification.getUserId().equals(userId)) {
            return Result.fail("无权查看此通知");
        }
        
        return Result.ok(notification);
    }

    /**
     * 检查SSE连接状态
     */
    @GetMapping("/sse/status")
    public Result<Map<String, Object>> getSseStatus() {
        Long userId = LoginUserInfoHelper.getUserId();
        boolean isOnline = sseEmitterService.isUserOnline(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("online", isOnline);
        result.put("userId", userId);
        
        return Result.ok(result);
    }
}
