package com.yunshang.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yunshang.auth.mapper.SysRoleMenuMapper;
import com.yunshang.auth.mapper.SysUserRoleMapper;
import com.yunshang.auth.service.SysUserService;
import com.yunshang.model.system.SysRoleMenu;
import com.yunshang.model.system.SysUser;
import com.yunshang.model.system.SysUserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 权限缓存管理服务，负责在权限变更时主动失效Redis中的权限缓存
 */
@Slf4j
@Service
public class PermissionCacheService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Resource
    private SysUserService sysUserService;

    /**
     * 删除指定用户的权限缓存
     */
    public void invalidateByUserId(Long userId) {
        SysUser user = sysUserService.getById(userId);
        if (user != null) {
            log.info("删除用户权限缓存，username: {}", user.getUsername());
            redisTemplate.delete(user.getUsername());
        }
    }

    /**
     * 删除指定角色下所有用户的权限缓存
     */
    public void invalidateByRoleId(Long roleId) {
        List<Long> userIds = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, roleId)
        ).stream().map(SysUserRole::getUserId).toList();

        deleteByUserIds(userIds);
    }

    /**
     * 删除拥有指定菜单的所有用户的权限缓存（菜单→角色→用户）
     */
    public void invalidateByMenuId(Long menuId) {
        // 找到拥有该菜单的所有角色
        List<Long> roleIds = sysRoleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMenuId, menuId)
        ).stream().map(SysRoleMenu::getRoleId).distinct().toList();

        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }

        // 找到这些角色下的所有用户
        List<Long> userIds = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getRoleId, roleIds)
        ).stream().map(SysUserRole::getUserId).distinct().toList();

        deleteByUserIds(userIds);
    }

    /**
     * 根据用户ID列表批量删除Redis权限缓存
     */
    private void deleteByUserIds(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        List<SysUser> users = sysUserService.listByIds(userIds);
        for (SysUser user : users) {
            log.info("删除用户权限缓存，username: {}", user.getUsername());
            redisTemplate.delete(user.getUsername());
        }
    }
}
