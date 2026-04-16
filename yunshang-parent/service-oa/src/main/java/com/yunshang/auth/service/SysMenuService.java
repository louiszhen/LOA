package com.yunshang.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunshang.model.system.SysMenu;
import com.yunshang.vo.system.AssignMenuVo;
import com.yunshang.vo.system.RouterVo;

import java.util.List;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-10 13:59
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 菜单树形数据
     */
    List<SysMenu> findNodes();

    /**
     * 删除菜单，如果有子菜单，则不能删除
     */
    void removeMenuById(Long id);

    /**
     * 根据角色获取菜单
     */
    List<SysMenu> findSysMenuByRoleId(Long roleId);

    /**
     * 给角色分配权限
     */
    void doAssign(AssignMenuVo assignMenuVo);

    /**
     * 获取用户菜单
     */
    List<RouterVo> findUserMenuList(Long userId);

    /**
     * 获取用户按钮权限
     */
    List<String> findUserPermissionsList(Long userId);
}
