package com.yunshang.auth.controller;

import com.yunshang.auth.service.SysMenuService;
import com.yunshang.auth.service.impl.PermissionCacheService;
import com.yunshang.common.result.Result;
import com.yunshang.model.system.SysMenu;
import com.yunshang.vo.system.AssignMenuVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-10 14:01
 */
@RestController
@RequestMapping("/admin/system/sysMenu")
@SuppressWarnings({"rawtypes"})
public class SysMenuController {

    @Resource
    private SysMenuService sysMenuService;

    @Resource
    private PermissionCacheService permissionCacheService;

    /**
     * 新增菜单
     */
    @PostMapping("save")
    public Result save(@RequestBody SysMenu permission) {
        sysMenuService.save(permission);
        return Result.ok();
    }

    /**
     * 修改菜单
     */
    @PutMapping("update")
    public Result updateById(@RequestBody SysMenu permission) {
        sysMenuService.updateById(permission);
        // 菜单信息变更后，失效拥有该菜单的所有用户的权限缓存
        permissionCacheService.invalidateByMenuId(permission.getId());
        return Result.ok();
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("hasAnyAuthority('bnt.sysMenu.remove')")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        sysMenuService.removeMenuById(id);
        return Result.ok();
    }

    /**
     * 获取菜单
     */
    @GetMapping("findNodes")
    public Result findNodes() {
        List<SysMenu> sysMenuList = sysMenuService.findNodes();
        return Result.ok(sysMenuList);
    }

    /**
     * 根据角色获取菜单
     */
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId) {
        List<SysMenu> list = sysMenuService.findSysMenuByRoleId(roleId);
        return Result.ok(list);
    }

    /**
     * 给角色分配权限
     */
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssignMenuVo assignMenuVo) {
        sysMenuService.doAssign(assignMenuVo);
        return Result.ok();
    }
}
