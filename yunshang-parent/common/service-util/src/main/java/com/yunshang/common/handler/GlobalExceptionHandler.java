package com.yunshang.common.handler;

import com.yunshang.common.exception.YunshangException;
import com.yunshang.common.result.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author nanfeng
 * @description 全局异常处理类
 * @date 2023-03-06 15:30
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(YunshangException.class)
    @ResponseBody
    public Result error(YunshangException e) {
        e.printStackTrace();
        return Result.fail().message(e.getMessage()).code(e.getCode());
    }

    /**
     * SpringSecurity方法访问权限异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Result error(AccessDeniedException e) {
        e.printStackTrace();
        return Result.fail().message("没有操作权限").code(205);
    }
}
