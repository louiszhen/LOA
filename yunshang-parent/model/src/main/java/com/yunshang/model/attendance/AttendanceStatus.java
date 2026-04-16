package com.yunshang.model.attendance;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 打卡状态枚举
 * @author louis
 * @date 2026-03-20
 */
@Getter
@AllArgsConstructor
public enum AttendanceStatus {

    NORMAL("normal", "正常"),
    LATE("late", "迟到"),
    EARLY_LEAVE("early_leave", "早退"),
    ABSENT("absent", "未打卡");

    private final String code;
    private final String desc;
}
