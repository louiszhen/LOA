package com.yunshang.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunshang.auth.mapper.SysRoleMapper;
import com.yunshang.auth.mapper.SysUserRoleMapper;
import com.yunshang.auth.service.SysRoleService;
import com.yunshang.model.system.SysRole;
import com.yunshang.model.system.SysUserRole;
import com.yunshang.vo.system.AssignRoleVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-06 15:06
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private PermissionCacheService permissionCacheService;

    @Override
    public Map<String, Object> findRoleByUserId(Long userId) {
        // 查询系统中所有的角色
        List<SysRole> allRolesList = this.list();

        // 查询当前用户所拥有的角色的id
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> existUserRoleList = sysUserRoleMapper.selectList(queryWrapper);
        List<Long> existRoleIdList = existUserRoleList.stream().map(SysUserRole::getRoleId).toList();

        // 查询用户的角色信息(方法一)
        //List<SysRole> assignRoleList = allRolesList.stream().filter(sysRole -> existRoleIdList.contains(sysRole.getId())).toList();

        //查询用户的角色信息(方法二)
        LambdaQueryWrapper<SysRole> roleQueryWrapper = new LambdaQueryWrapper<>();
        roleQueryWrapper.in(existRoleIdList != null && !existRoleIdList.isEmpty(), SysRole::getId, existRoleIdList);
        List<SysRole> assignRoleList = sysRoleMapper.selectList(roleQueryWrapper);

        // 封装数据返回，分别是所有角色和当前用户角色
        Map<String, Object> map = new HashMap<>();
        map.put("assignRoleList", assignRoleList);
        map.put("allRolesList", allRolesList);
        return map;
    }

    @Override
    @Transactional
    public void doAssign(AssignRoleVo assignRoleVo) {
        // 首先删除该用户的所有角色信息
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, assignRoleVo.getUserId());
        sysUserRoleMapper.delete(queryWrapper);

        // 然后再重新赋值
        List<Long> roleIdList = assignRoleVo.getRoleIdList();
        if (roleIdList != null && !roleIdList.isEmpty()) {
            List<SysUserRole> userRoleList = new ArrayList<>();
            for (Long roleId : roleIdList) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(assignRoleVo.getUserId());
                sysUserRole.setRoleId(roleId);
                userRoleList.add(sysUserRole);
                //sysUserRoleMapper.insert(sysUserRole);
            }
            sysUserRoleMapper.insertBatch(userRoleList);
        }

        // 用户角色变更后，删除该用户的Redis权限缓存
        permissionCacheService.invalidateByUserId(assignRoleVo.getUserId());
    }
}
