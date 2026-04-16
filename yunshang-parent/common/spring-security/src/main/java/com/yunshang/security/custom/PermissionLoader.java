package com.yunshang.security.custom;

import java.util.List;

/**
 * 权限加载接口，用于在Redis缓存失效时从数据库降级加载权限数据
 */
public interface PermissionLoader {

    /**
     * 根据用户ID加载权限标识列表
     *
     * @param userId 用户ID
     * @return 权限标识列表
     */
    List<String> loadPermissions(Long userId);
}
