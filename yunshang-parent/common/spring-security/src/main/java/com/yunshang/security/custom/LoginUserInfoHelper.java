package com.yunshang.security.custom;

/**
 * @author nanfeng
 * @description 获取当前用户信息帮助类
 * @date 2023-03-18 20:34
 */
public class LoginUserInfoHelper {

    private static final ThreadLocal<Long> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> username = new ThreadLocal<>();

    public static Long getUserId() {
        return userId.get();
    }

    public static void setUserId(Long _userId) {
        userId.set(_userId);
    }

    public static void removeUserId() {
        userId.remove();
    }

    public static String getUsername() {
        return username.get();
    }

    public static void setUsername(String _username) {
        username.set(_username);
    }

    public static void removeUsername() {
        username.remove();
    }
}
