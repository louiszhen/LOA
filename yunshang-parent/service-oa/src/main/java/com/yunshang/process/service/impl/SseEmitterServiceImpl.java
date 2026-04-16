package com.yunshang.process.service.impl;

import com.alibaba.fastjson2.JSON;
import com.yunshang.process.service.SseEmitterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @description SSE实时推送服务实现
 * @author 
 * @date 2026-03-24
 */
@Slf4j
@Service
public class SseEmitterServiceImpl implements SseEmitterService {

    /** SSE连接超时时间: 30分钟 */
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;

    /** 用户SSE连接Map，key为userId */
    private static final Map<Long, SseEmitter> USER_EMITTER_MAP = new ConcurrentHashMap<>();

    @Override
    public SseEmitter connect(Long userId) {
        // 如果用户已存在连接，先关闭旧连接
        SseEmitter oldEmitter = USER_EMITTER_MAP.get(userId);
        if (oldEmitter != null) {
            try {
                oldEmitter.complete();
            } catch (Exception e) {
                log.warn("关闭旧SSE连接时发生异常，userId: {}", userId);
            }
            USER_EMITTER_MAP.remove(userId);
        }

        // 创建新的SSE连接
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        
        // 设置回调
        emitter.onCompletion(() -> {
            log.info("SSE连接完成，userId: {}", userId);
            USER_EMITTER_MAP.remove(userId);
        });
        
        emitter.onTimeout(() -> {
            log.info("SSE连接超时，userId: {}", userId);
            USER_EMITTER_MAP.remove(userId);
        });
        
        emitter.onError(e -> {
            log.error("SSE连接异常，userId: {}, error: {}", userId, e.getMessage());
            USER_EMITTER_MAP.remove(userId);
        });

        // 保存连接
        USER_EMITTER_MAP.put(userId, emitter);
        log.info("SSE连接已建立，userId: {}, 当前在线用户数: {}", userId, USER_EMITTER_MAP.size());

        try {
            // 发送初始连接成功消息
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("SSE连接建立成功"));
        } catch (IOException e) {
            log.error("发送SSE初始消息失败，userId: {}", userId, e);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    @Override
    public void disconnect(Long userId) {
        SseEmitter emitter = USER_EMITTER_MAP.remove(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("disconnect")
                        .data("SSE连接关闭"));
                emitter.complete();
            } catch (IOException e) {
                log.error("关闭SSE连接时发生异常，userId: {}", userId, e);
            }
            log.info("SSE连接已断开，userId: {}", userId);
        }
    }

    @Override
    public void sendToUser(Long userId, Object message) {
        SseEmitter emitter = USER_EMITTER_MAP.get(userId);
        if (emitter == null) {
            log.debug("用户未建立SSE连接，跳过推送，userId: {}", userId);
            return;
        }

        try {
            String jsonMessage = JSON.toJSONString(message);
            emitter.send(SseEmitter.event()
                    .name("message")
                    .data(jsonMessage, MediaType.APPLICATION_JSON));
            log.debug("SSE消息推送成功，userId: {}", userId);
        } catch (IOException e) {
            log.error("SSE消息推送失败，userId: {}", userId, e);
            // 移除失效连接
            USER_EMITTER_MAP.remove(userId);
            try {
                emitter.completeWithError(e);
            } catch (Exception ex) {
                log.error("SSE连接完成时发生异常，userId: {}", userId, ex);
            }
        }
    }

    @Override
    public void sendNotificationToUser(Long userId, Object notification) {
        SseEmitter emitter = USER_EMITTER_MAP.get(userId);
        if (emitter == null) {
            log.debug("用户未建立SSE连接，跳过通知推送，userId: {}", userId);
            return;
        }

        try {
            String jsonNotification = JSON.toJSONString(notification);
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(jsonNotification, MediaType.APPLICATION_JSON));
            log.info("SSE通知推送成功，userId: {}", userId);
        } catch (IOException e) {
            log.error("SSE通知推送失败，userId: {}", userId, e);
            // 移除失效连接
            USER_EMITTER_MAP.remove(userId);
            try {
                emitter.completeWithError(e);
            } catch (Exception ex) {
                log.error("SSE连接完成时发生异常，userId: {}", userId, ex);
            }
        }
    }

    @Override
    public void broadcast(Object message) {
        log.info("开始广播消息，当前在线用户数: {}", USER_EMITTER_MAP.size());
        USER_EMITTER_MAP.forEach((userId, emitter) -> {
            try {
                String jsonMessage = JSON.toJSONString(message);
                emitter.send(SseEmitter.event()
                        .name("broadcast")
                        .data(jsonMessage, MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                log.error("广播消息推送失败，userId: {}", userId, e);
                USER_EMITTER_MAP.remove(userId);
            }
        });
    }

    @Override
    public boolean isUserOnline(Long userId) {
        return USER_EMITTER_MAP.containsKey(userId);
    }

    @Override
    public void sendHeartbeat() {
        USER_EMITTER_MAP.forEach((userId, emitter) -> {
            try {
                Map<String, Object> heartbeat = new HashMap<>();
                heartbeat.put("type", "heartbeat");
                heartbeat.put("timestamp", System.currentTimeMillis());
                String jsonHeartbeat = JSON.toJSONString(heartbeat);
                emitter.send(SseEmitter.event()
                        .name("heartbeat")
                        .data(jsonHeartbeat, MediaType.APPLICATION_JSON));
                log.debug("SSE心跳发送成功，userId: {}", userId);
            } catch (IOException e) {
                log.warn("SSE心跳发送失败，移除失效连接，userId: {}", userId);
                USER_EMITTER_MAP.remove(userId);
            }
        });
        if (!USER_EMITTER_MAP.isEmpty()) {
            log.info("SSE心跳轮次完成，当前在线用户数: {}", USER_EMITTER_MAP.size());
        }
    }
}
