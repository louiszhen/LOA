package com.yunshang.auth.service.impl;

import com.yunshang.auth.service.SysMenuService;
import com.yunshang.auth.service.SysUserService;
import com.yunshang.common.exception.YunshangException;
import com.yunshang.model.system.SysUser;
import com.yunshang.security.custom.CustomUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-14 20:30
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户名不存在！");
        }
        if (sysUser.getStatus() == 0) {
            throw new YunshangException(201, "该账号已停用！");
        }

        // 查询用户权限数据并封装
        List<String> userPermissionsList = sysMenuService.findUserPermissionsList(sysUser.getId());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String permission : userPermissionsList) {
            authorities.add(new SimpleGrantedAuthority(permission.trim()));
        }

        return new CustomUser(sysUser, authorities);
    }
}
