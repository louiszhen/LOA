package com.yunshang.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunshang.common.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author nanfeng
 * @description 有些方法不在controller中，所以返回数据只能调用原生的方法进行返回，这里进行封装
 * @date 2023-03-14 20:50
 */
public class ResponseUtil {

    public static void out(HttpServletResponse response, Result result) {
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        try {
            mapper.writeValue(response.getWriter(), result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
