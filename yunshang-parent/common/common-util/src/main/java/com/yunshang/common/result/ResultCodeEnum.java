package com.yunshang.common.result;

import lombok.Getter;

/**
 * @author nanfeng
 * @description 统一返回结果状态信息类
 * @date 2023-03-06 14:49
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务异常"),
    DATA_ERROR(204, "数据异常"),
    LOGIN_ERROR(208, "未登录"),
    NO_PERMISSION(209, "没有权限"),
    MEETING_TIME_CONFLICT(210, "会议时间存在冲突"),
    MEETING_ROOM_EXIST(211, "会议室已存在");

    private final Integer code;

    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
