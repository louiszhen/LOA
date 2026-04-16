package com.yunshang.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunshang.auth.mapper.SysMenuMapper;
import com.yunshang.auth.mapper.SysRoleMenuMapper;
import com.yunshang.auth.service.SysMenuService;
import com.yunshang.auth.util.MenuHelper;
import com.yunshang.common.exception.YunshangException;
import com.yunshang.model.system.SysMenu;
import com.yunshang.model.system.SysRoleMenu;
import com.yunshang.vo.system.AssignMenuVo;
import com.yunshang.vo.system.MetaVo;
import com.yunshang.vo.system.RouterVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-10 14:00
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Resource
    private PermissionCacheService permissionCacheService;

    @Override
    public List<SysMenu> findNodes() {
        // 1.查询所有的菜单数据
        List<SysMenu> sysMenuList = sysMenuMapper.selectList(null);
        // 2.构造树型结构
        return MenuHelper.buildTree(sysMenuList);
    }

    @Override
    public void removeMenuById(Long id) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getParentId, id);
        Long count = sysMenuMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new YunshangException(201, "当前菜单含有子菜单，不可以删除！");
        }
        // 删除菜单前，先失效拥有该菜单的所有用户的权限缓存
        permissionCacheService.invalidateByMenuId(id);
        sysMenuMapper.deleteById(id);
    }

    @Override
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {
        // 获取所有菜单，添加条件 status=1
        List<SysMenu> sysMenuList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));

        // 根据角色id获取对应的角色权限
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuMapper.selectList(queryWrapper);

        // 根据获取的菜单id，获取对应的菜单对象
        List<Long> menuIdList = sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).toList();
        sysMenuList.forEach(sysMenu -> {
            sysMenu.setSelect(menuIdList.contains(sysMenu.getId()));
        });

        return MenuHelper.buildTree(sysMenuList);
    }

    @Override
    @Transactional
    public void doAssign(AssignMenuVo assignMenuVo) {
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<SysRoleMenu>();
        queryWrapper.eq(SysRoleMenu::getRoleId, assignMenuVo.getRoleId());
        sysRoleMenuMapper.delete(queryWrapper);

        for (Long menuId : assignMenuVo.getMenuIdList()) {
            if (!StringUtils.hasLength(String.valueOf(menuId))) {
                continue;
            }
            SysRoleMenu rolePermission = new SysRoleMenu();
            rolePermission.setRoleId(assignMenuVo.getRoleId());
            rolePermission.setMenuId(menuId);
            sysRoleMenuMapper.insert(rolePermission);
        }

        // 角色权限变更后，删除该角色下所有用户的Redis权限缓存
        permissionCacheService.invalidateByRoleId(assignMenuVo.getRoleId());
    }

    @Override
    public List<RouterVo> findUserMenuList(Long userId) {
        List<SysMenu> sysMenuList = null;

        // 管理员admin的id为1
        if (userId == 1) {
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysMenu::getStatus, 1)
                    .orderByAsc(SysMenu::getSortValue);
            sysMenuList = this.list(queryWrapper);
        } else {
            sysMenuList = sysMenuMapper.findListByUserId(userId);
        }

        // 构造树型数据
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenuList);

        return this.buildMenus(sysMenuTreeList);
    }

    @Override
    public List<String> findUserPermissionsList(Long userId) {
        List<SysMenu> sysMenuList;

        // 超级管理员admin的id为1
        if (userId == 1) {
            sysMenuList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));
        } else {
            sysMenuList = sysMenuMapper.findListByUserId(userId);
        }
        return sysMenuList.stream().filter(item -> item.getType() == 2).map(SysMenu::getPerms).toList();
    }

    /**
     * 根据菜单列表构建成框架所需的路由结构
     */
    private List<RouterVo> buildMenus(List<SysMenu> menuList) {
        List<RouterVo> routerVoList = new LinkedList<>();

        for (SysMenu menu : menuList) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            List<SysMenu> menuChildren = menu.getChildren();

            // 如果当前是菜单，需将按钮对应的路由加载出来，如："角色授权"按钮对应的路由在"系统管理"下面
            if (menu.getType() == 1) {
                // 加载隐藏路由
                List<SysMenu> hiddenMenuList = menuChildren.stream()
                        .filter(children -> StringUtils.hasLength(children.getComponent())).toList();
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routerVoList.add(hiddenRouter);
                }
            } else {
                if (!CollectionUtils.isEmpty(menuChildren)) {
                    if (menuChildren.size() > 0) {
                        router.setAlwaysShow(true);
                    }
                    router.setChildren(buildMenus(menuChildren));
                }
            }
            routerVoList.add(router);
        }
        return routerVoList;
    }

    /**
     * 获取路由地址
     */
    public String getRouterPath(SysMenu sysMenu) {
        return sysMenu.getParentId() != 0 ? sysMenu.getPath() : "/" + sysMenu.getPath();
    }
}
