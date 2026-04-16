//package com.yunshang.wechat.service;
//
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONObject;
//import com.yunshang.auth.service.SysUserService;
//import com.yunshang.model.process.Process;
//import com.yunshang.model.process.ProcessTemplate;
//import com.yunshang.model.system.SysUser;
//import com.yunshang.process.service.ProcessService;
//import com.yunshang.process.service.ProcessTemplateService;
//import com.yunshang.security.custom.LoginUserInfoHelper;
//import lombok.extern.slf4j.Slf4j;
//import me.chanjar.weixin.common.error.WxErrorException;
//import me.chanjar.weixin.mp.api.WxMpService;
//import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
//import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
//import org.joda.time.DateTime;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import javax.annotation.Resource;
//import java.util.Date;
//import java.util.Map;
//
///**
// * @author nanfeng
// * @description TODO
// * @date 2023-03-22 21:32
// */
//@Service
//@Slf4j
//public class WechatMessageServiceImpl implements WechatMessageService {
//
//    @Resource
//    private WxMpService wxMpService;
//
//    @Resource
//    private ProcessService processService;
//
//    @Resource
//    private ProcessTemplateService processTemplateService;
//
//    @Resource
//    private SysUserService sysUserService;
//
//    @Override
//    public void pushPendingMessage(Long processId, Long userId, String taskId) throws WxErrorException {
//        Process process = processService.getById(processId);
//        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
//        // 处理审批任务的用户
//        SysUser processUser = sysUserService.getById(userId);
//        // 发起审批任务的用户
//        SysUser submitUser = sysUserService.getById(process.getUserId());
//        String processUserOpenId = processUser.getOpenId();
//        if (!StringUtils.hasText(processUserOpenId)) {
//            // 当前用户尚未与微信绑定，将待处理消息转发给管理员的微信
//            processUserOpenId = "oxMcn6BFrOUVSbQurUaPRcCaJaS0";
//        }
//        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
//                .toUser(processUserOpenId)
//                .templateId("KvOVeW7jz4-DZgQ_WuXjMZO5I4pPA7L7fflVNwC_ZQg") // 推送消息的模版id
//                .url("http://kvjdgd.natappfree.cc/#/show/" + processId + "/" + taskId) // 点击模板消息要访问的网址
//                .build();
//
//        StringBuilder content = fromValue2Json(process);
//        templateMessage.addData(new WxMpTemplateData("first", submitUser.getName() + "提交了" + processTemplate.getName() + "审批申请，请注意查看。", "#272727"));
//        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
//        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
//        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));
//
//        String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
//        log.info("推送消息返回：{}", msg);
//    }
//
//    @Override
//    public void pushProcessedMessage(Long processId, Long userId, Integer status) throws WxErrorException {
//        Process process = processService.getById(processId);
//        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
//        // 审批流程发起者
//        SysUser initiator = sysUserService.getById(userId);
//        // 当前处理完流程的用户
//        SysUser processor = sysUserService.getById(LoginUserInfoHelper.getUserId());
//        String openId = initiator.getOpenId();
//        if (!StringUtils.hasText(openId)) {
//            openId = "oxMcn6BFrOUVSbQurUaPRcCaJaS0";
//        }
//        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
//                .toUser(openId)
//                .templateId("KvOVeW7jz4-DZgQ_WuXjMZO5I4pPA7L7fflVNwC_ZQg") // 推送消息的模版id
//                .url("http://kvjdgd.natappfree.cc/#/show/" + processId + "/0") // 点击模板消息要访问的网址
//                .build();
//
//        StringBuilder content = fromValue2Json(process);
//        templateMessage.addData(new WxMpTemplateData("first", "你发起的"+processTemplate.getName()+"审批申请已经被处理了，请注意查看。", "#272727"));
//        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
//        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
//        templateMessage.addData(new WxMpTemplateData("keyword3", processor.getName(), "#272727"));
//        templateMessage.addData(new WxMpTemplateData("keyword4", status == 1 ? "审批通过" : "审批拒绝", status == 1 ? "#009966" : "#FF0033"));
//        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));
//        String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
//        log.info("推送消息返回：{}", msg);
//    }
//
//    private StringBuilder fromValue2Json(Process process) {
//        StringBuilder content = new StringBuilder();
//        
//        JSONObject formValuesObj = JSON.parseObject(process.getFormValues());
//        if (formValuesObj == null) {
//            return content;
//        }
//        
//        JSONObject formShowData = formValuesObj.getJSONObject("formShowData");
//        if (formShowData == null || formShowData.isEmpty()) {
//            return content;
//        }
//        
//        for (Map.Entry<String, Object> entry : formShowData.entrySet()) {
//            content.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
//        }
//
//        return content;
//    }
//}
