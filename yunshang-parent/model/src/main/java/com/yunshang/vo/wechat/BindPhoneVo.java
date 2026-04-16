package com.yunshang.vo.wechat;

import lombok.Data;

@Data
public class BindPhoneVo {

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 微信提供的用户id
     */
    private String openId;
}
