package com.yunshang.model.attendance;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 打卡类型枚举
 * @author louis
 * @date 2026-03-20
 */
@Getter
@AllArgsConstructor
public enum ClockType {

    MORNING("morning", "上班打卡"),
    EVENING("evening", "下班打卡");

    private final String code;
    private final String desc;
}
