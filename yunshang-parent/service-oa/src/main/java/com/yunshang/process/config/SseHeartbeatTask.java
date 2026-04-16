package com.yunshang.process.config;

import com.yunshang.process.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * SSE心跳定时任务
 * 每20分钟向所有在线用户发送心跳，防止连接超时失效
 *
 * @author 
 * @date 2026-03-24
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class SseHeartbeatTask {

    private final SseEmitterService sseEmitterService;

    /**
     * 每20分钟发送一次心跳
     * fixedRate = 20 * 60 * 1000 = 1200000ms
     * 首次延迟5秒执行，避免应用启动时立即执行
     */
    @Scheduled(fixedRate = 1200000, initialDelay = 5000)
    public void sendHeartbeat() {
        log.debug("开始执行SSE心跳任务...");
        sseEmitterService.sendHeartbeat();
    }
}
