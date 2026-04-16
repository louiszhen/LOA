package com.yunshang.auth.service.impl;

import com.yunshang.auth.service.SysMenuService;
import com.yunshang.security.custom.PermissionLoader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 权限加载实现，供TokenAuthenticationFilter在Redis缓存失效时降级查库使用
 */
@Component("permissionLoader")
public class PermissionLoaderImpl implements PermissionLoader {

    @Resource
    private SysMenuService sysMenuService;

    @Override
    public List<String> loadPermissions(Long userId) {
        return sysMenuService.findUserPermissionsList(userId);
    }
}
