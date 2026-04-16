package com.yunshang.wechat.controller;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yunshang.auth.service.SysUserService;
import com.yunshang.common.jwt.JwtHelper;
import com.yunshang.common.result.Result;
import com.yunshang.model.system.SysUser;
import com.yunshang.vo.wechat.BindPhoneVo;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-22 15:34
 */
@Controller
@RequestMapping("/admin/wechat")
@Slf4j
@SuppressWarnings("rawtypes")
@CrossOrigin // 开启跨域功能
public class WechatController {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private WxMpService wxMpService;

    @Value("${wechat.user-info-url}")
    private String userInfoUrl;

    /**
     * 微信授权登录功能
     *
     * @param returnUrl 授权成功之后，在微信端页面应该返回的路径
     */
    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl) {
        // 由于授权回调成功后，要返回原地址路径，原地址路径带#，URL传参时，参数包含有特殊字符（%、#、&）是不能直接传递的
        // 前端在传递路径时，将路径中的#替换成了yunshang，这里需要替换回来

        // 参数1：授权路径，在哪个路径获取微信信息
        // 参数2：固定值，授权类型
        // 参数3：授权成功之后跳转的路径
        String authType = WxConsts.OAuth2Scope.SNSAPI_USERINFO;
        returnUrl = URLEncoder.encode(returnUrl.replace("yunshang", "#"), StandardCharsets.UTF_8);
        String redirectURL = wxMpService.getOAuth2Service().buildAuthorizationUrl(userInfoUrl, authType, returnUrl);
        log.info("【微信网页授权】获取redirectURL={}", redirectURL);
        return "redirect:" + redirectURL;
    }

    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) throws Exception {
        log.info("【微信网页授权】code={}", code);
        log.info("【微信网页授权】state={}", returnUrl);

        // 获取accessToken，进而获取用户的openId
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
        String openId = accessToken.getOpenId();
        log.info("【微信网页授权】openId={}", openId);

        // 获取微信用户信息
        WxOAuth2UserInfo wxMpUser = wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
        log.info("【微信网页授权】wxMpUser={}", JSON.toJSONString(wxMpUser));

        // 根据openId查询系统用户
        SysUser sysUser = sysUserService.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getOpenId, openId));
        String token = "";
        // 说明已经绑定，反之为建立账号绑定，去页面建立账号绑定
        if (sysUser != null) {
            // 已经绑定，生成token返回的指定url
            token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        }

        returnUrl = returnUrl.contains("?")
                ? returnUrl + "&token=" + token + "&openId=" + openId
                : returnUrl + "?token=" + token + "&openId=" + openId;

        return "redirect:" + returnUrl;
    }

    /**
     * 微信账号绑定手机
     */
    @PostMapping("bindPhone")
    @ResponseBody
    public Result bindPhone(@RequestBody BindPhoneVo bindPhoneVo) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPhone, bindPhoneVo.getPhone());
        SysUser sysUser = sysUserService.getOne(queryWrapper);

        if (sysUser == null) {
            return Result.fail("手机号在系统中不存在，请联系系统管理员进行存储或者更新！");
        } else {
            // 存在用户，更新其openId
            sysUser.setOpenId(bindPhoneVo.getOpenId());
            sysUserService.updateById(sysUser);
            String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
            return Result.ok(token);
        }
    }
}
