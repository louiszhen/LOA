package com.yunshang.common.exception;

import com.yunshang.common.result.ResultCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author nanfeng
 * @description 自定义全局异常类
 * @date 2023-03-06 15:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class YunshangException extends RuntimeException {

    private Integer code;

    private String message;

    /**
     * 通过状态码和错误消息创建异常对象
     */
    public YunshangException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 接收枚举类型对象
     */
    public YunshangException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }
}
