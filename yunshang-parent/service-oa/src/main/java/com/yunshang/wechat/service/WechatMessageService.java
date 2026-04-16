package com.yunshang.wechat.service;

import me.chanjar.weixin.common.error.WxErrorException;

/**
 * @author nanfeng
 * @description 微信消息推送通知服务类
 * @date 2023-03-22 21:29
 */
public interface WechatMessageService {

    /**
     * 推送给待审批人员
     */
    void pushPendingMessage(Long processId, Long userId, String taskId) throws WxErrorException;

    /**
     * 审批后推送给提交审批的人
     */
    void pushProcessedMessage(Long processId, Long userId, Integer status) throws WxErrorException;
}
