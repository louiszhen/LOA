package com.yunshang.process.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @description SSE实时推送服务接口
 * @author 
 * @date 2026-03-24
 */
public interface SseEmitterService {

    /**
     * 建立SSE连接
     *
     * @param userId 用户ID
     * @return SseEmitter对象
     */
    SseEmitter connect(Long userId);

    /**
     * 关闭SSE连接
     *
     * @param userId 用户ID
     */
    void disconnect(Long userId);

    /**
     * 推送消息给指定用户
     *
     * @param userId  用户ID
     * @param message 消息内容
     */
    void sendToUser(Long userId, Object message);

    /**
     * 推送新任务通知给指定用户
     *
     * @param userId       用户ID
     * @param notification 通知对象
     */
    void sendNotificationToUser(Long userId, Object notification);

    /**
     * 广播消息给所有在线用户
     *
     * @param message 消息内容
     */
    void broadcast(Object message);

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    boolean isUserOnline(Long userId);

    /**
     * 发送心跳给所有在线用户
     */
    void sendHeartbeat();

    /**
     * 建立待审核列表专用SSE连接
     * 与普通connect不同，此连接专门用于接收待审核列表的增量更新
     *
     * @param userId 用户ID
     * @return SseEmitter对象
     */
    SseEmitter connectPending(Long userId);

    /**
     * 推送新的待审核任务给指定用户
     * 推送的ProcessVo与/admin/process/findPending/{page}/{limit}接口返回字段完全一致
     *
     * @param userId     用户ID
     * @param processVo  待审核任务数据（ProcessVo对象）
     */
    void sendNewPendingToUser(Long userId, Object processVo);
}
