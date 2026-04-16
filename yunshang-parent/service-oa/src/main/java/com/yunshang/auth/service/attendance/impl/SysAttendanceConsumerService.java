package com.yunshang.auth.service.attendance.impl;

import com.yunshang.vo.attendance.AttendanceMessageVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 考勤消息消费者服务（本地缓冲 + 双重触发批量落盘）
 *
 * 触发条件（满足任一即刷盘）：
 *   1. 本地缓冲积累到 BATCH_SIZE 条
 *   2. 距上次刷盘超过 FLUSH_INTERVAL_MS 毫秒
 *
 * @author louis
 * @date 2026-03-20
 */
@Slf4j
@Service
public class SysAttendanceConsumerService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SysAttendanceMessageHandler messageHandler;

    /** Redisson 队列名称 */
    private static final String ATTENDANCE_QUEUE_NAME = "attendance:queue";

    /** 从 Redisson 队列拉取消息的线程数 */
    private static final int CONSUMER_THREAD_POOL_SIZE = 1;

    /** 批量落盘阈值：缓冲满 50 条立即刷盘 */
    private static final int BATCH_SIZE = 50;

    /** 时间触发阈值：最多等待 5 秒，避免尾巴消息长时间不落盘 */
    private static final long FLUSH_INTERVAL_MS = 5_000L;

    /** 应用关闭时等待刷盘的最大时间 */
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 30;

    /**
     * 本地缓冲：线程安全的阻塞队列，仅由单一消费线程写入，定时器线程与消费线程共同读取并清空。
     * 使用 LinkedBlockingQueue 保证 offer/drainTo 的线程安全。
     */
    private final LinkedBlockingQueue<AttendanceMessageVo> localBuffer = new LinkedBlockingQueue<>();

    /** 上次刷盘时间戳（毫秒），volatile 保证多线程可见 */
    private volatile long lastFlushTime = System.currentTimeMillis();

    private ExecutorService consumerExecutor;
    private ScheduledExecutorService schedulerExecutor;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        running.set(true);

        // 1. 消费线程：从 Redisson 队列拉消息 → 放入本地缓冲 → 数量满时触发刷盘
        consumerExecutor = Executors.newFixedThreadPool(CONSUMER_THREAD_POOL_SIZE);
        for (int i = 0; i < CONSUMER_THREAD_POOL_SIZE; i++) {
            consumerExecutor.submit(this::consumeLoop);
        }

        // 2. 定时线程：每 FLUSH_INTERVAL_MS 检查缓冲区，非空则触发刷盘（时间触发）
        schedulerExecutor = Executors.newSingleThreadScheduledExecutor();
        schedulerExecutor.scheduleAtFixedRate(
                this::timeTriggerFlush,
                FLUSH_INTERVAL_MS,
                FLUSH_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );

        log.info("考勤消息消费者已启动 [batchSize={}, flushInterval={}ms]", BATCH_SIZE, FLUSH_INTERVAL_MS);
    }

    @PreDestroy
    public void destroy() {
        running.set(false);

        // 先停定时器
        shutdownExecutor(schedulerExecutor, "定时刷盘");
        // 再停消费线程
        shutdownExecutor(consumerExecutor, "消费者");

        // 最后将缓冲区残留消息强制刷盘，避免丢失
        if (!localBuffer.isEmpty()) {
            log.info("关闭前强制刷盘，剩余消息数: {}", localBuffer.size());
            doFlush();
        }

        log.info("考勤消息消费者已关闭");
    }

    // -------------------------------------------------------------------------
    // 私有方法
    // -------------------------------------------------------------------------

    /**
     * 消费循环：持续从 Redisson 队列拉取消息，写入本地缓冲。
     * 积累到 BATCH_SIZE 条时触发数量刷盘。
     */
    private void consumeLoop() {
        RBlockingQueue<AttendanceMessageVo> queue = redissonClient.getBlockingQueue(ATTENDANCE_QUEUE_NAME);

        while (running.get()) {
            try {
                // 最多阻塞 2 秒，避免 running=false 后线程无法退出
                AttendanceMessageVo message = queue.poll(2, TimeUnit.SECONDS);
                if (message == null) {
                    continue;
                }

                localBuffer.offer(message);

                // 数量触发：缓冲满 BATCH_SIZE 立即刷盘
                if (localBuffer.size() >= BATCH_SIZE) {
                    log.debug("数量触发刷盘，当前缓冲: {} 条", localBuffer.size());
                    doFlush();
                }

            } catch (InterruptedException e) {
                log.warn("消费者线程被中断");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("消费考勤消息异常，1秒后重试", e);
                sleepQuietly(1_000);
            }
        }
    }

    /**
     * 时间触发刷盘：由定时器每 FLUSH_INTERVAL_MS 调用一次。
     * 缓冲区非空 且 距上次刷盘超过阈值时才执行，避免与数量触发重复刷盘。
     */
    private void timeTriggerFlush() {
        if (localBuffer.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastFlushTime >= FLUSH_INTERVAL_MS) {
            log.debug("时间触发刷盘，当前缓冲: {} 条", localBuffer.size());
            doFlush();
        }
    }

    /**
     * 执行刷盘：将本地缓冲中所有消息排出，委托 Handler 批量落盘。
     * synchronized 确保同一时刻只有一个线程执行刷盘，防止重复消费。
     */
    private synchronized void doFlush() {
        if (localBuffer.isEmpty()) {
            return;
        }
        List<AttendanceMessageVo> batch = new ArrayList<>();
        localBuffer.drainTo(batch);
        if (batch.isEmpty()) {
            return;
        }
        lastFlushTime = System.currentTimeMillis();
        try {
            log.info("开始批量落盘，消息数: {}", batch.size());
            messageHandler.processBatch(batch);
            log.info("批量落盘完成，消息数: {}", batch.size());
        } catch (Exception e) {
            log.error("批量落盘失败，消息数: {}", batch.size(), e);
            // 落盘失败：将消息重新放回缓冲，等待下次重试
            batch.forEach(localBuffer::offer);
        }
    }

    private void shutdownExecutor(ExecutorService executor, String name) {
        if (executor == null || executor.isShutdown()) {
            return;
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                log.warn("{}线程池未能在 {}s 内正常关闭，已强制终止", name, SHUTDOWN_TIMEOUT_SECONDS);
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
