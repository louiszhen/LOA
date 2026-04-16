package com.yunshang.auth.util;

import com.yunshang.model.system.SysMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-10 14:11
 */
public class MenuHelper {

    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
        if (sysMenuList == null || sysMenuList.size() == 0) {
            return null;
        }

        // 创建list集合，用于封装最终数据
        List<SysMenu> menuTree = new ArrayList<>();

        for (SysMenu sysMenu : sysMenuList) {
            if (sysMenu.getParentId() == 0) {
                menuTree.add(getChildren(sysMenu, sysMenuList));
            }
        }
        return menuTree;
    }

    private static SysMenu getChildren(SysMenu sysMenu, List<SysMenu> treeNodes) {

        sysMenu.setChildren(new ArrayList<>());

        for (SysMenu treeNode : treeNodes) {
            if (Objects.equals(treeNode.getParentId(), sysMenu.getId())) {
                if (sysMenu.getChildren() == null) {
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(getChildren(treeNode, treeNodes));
            }
        }

        return sysMenu;
    }
}
