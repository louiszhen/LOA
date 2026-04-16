package com.yunshang.auth.controller;

import com.yunshang.auth.service.SysUserService;
import com.yunshang.common.exception.YunshangException;
import com.yunshang.common.jwt.JwtHelper;
import com.yunshang.common.result.Result;
import com.yunshang.common.utils.MD5;
import com.yunshang.model.system.SysUser;
import com.yunshang.vo.system.LoginVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nanfeng
 * @description 后台登录管理
 * @date 2023-03-07 19:03
 */
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 登录
     */
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {
        SysUser user = sysUserService.getByUsername(loginVo.getUsername());
        if (user == null) {
            throw new YunshangException(201, "该用户不存在！");
        }
        if (!MD5.encrypt(loginVo.getPassword()).equals(user.getPassword())) {
            throw new YunshangException(201, "密码错误");
        }
        if (user.getStatus() == 0) {
            throw new YunshangException(201, "用户被禁用");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("token", JwtHelper.createToken(user.getId(), user.getUsername()));
        return Result.ok(map);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("info")
    public Result info(HttpServletRequest request) {
        String username = JwtHelper.getUsername(request.getHeader("token"));
        Map<String, Object> map = sysUserService.getUserInfo(username);
        return Result.ok(map);
    }

    /**
     * 退出
     */
    @PostMapping("logout")
    public Result logout() {
        return Result.ok();
    }
}
