package com.yunshang.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunshang.auth.mapper.SysDeptUserMapper;
import com.yunshang.auth.mapper.SysUserMapper;
import com.yunshang.auth.mapper.SysUserRoleMapper;
import com.yunshang.auth.service.SysDeptService;
import com.yunshang.auth.service.SysDeptUserService;
import com.yunshang.auth.service.SysMenuService;
import com.yunshang.auth.service.SysRoleService;
import com.yunshang.auth.service.SysUserService;
import com.yunshang.common.utils.MD5;
import com.yunshang.model.system.*;
import com.yunshang.security.custom.LoginUserInfoHelper;
import com.yunshang.vo.system.RouterVo;
import com.yunshang.vo.system.SysUserQueryVo;
import com.yunshang.vo.system.SysUserVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-08 19:13
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private SysMenuService sysMenuService;

    @Resource
    private SysDeptUserService sysDeptUserService;

    @Resource
    private SysDeptService sysDeptService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 保存用户（密码MD5加密）
     */
    @Override
    public boolean save(SysUser user) {
        if (StringUtils.hasLength(user.getPassword())) {
            user.setPassword(MD5.encrypt(user.getPassword()));
        }
        return super.save(user);
    }

    /**
     * 更新用户（密码MD5加密）
     */
    @Override
    public boolean updateById(SysUser user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(MD5.encrypt(user.getPassword()));
        }
        return super.updateById(user);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser user = this.getById(id);
        user.setStatus(status);
        this.updateById(user);
    }

    @Override
    public SysUser getByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public Map<String, Object> getUserInfo(String username) {
        Map<String, Object> result = new HashMap<>();
        SysUser sysUser = this.getByUsername(username);

        // 根据用户id获取菜单权限
        List<RouterVo> routerVoList = sysMenuService.findUserMenuList(sysUser.getId());
        // 根据用户id获取用户按钮权限
        List<String> permsList = sysMenuService.findUserPermissionsList(sysUser.getId());

        result.put("name", sysUser.getName());
        result.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        // 当前权限控制使用不到，我们暂时忽略
        result.put("roles", new HashSet<>());
        result.put("buttons", permsList);
        result.put("routers", routerVoList);
        return result;
    }

    @Override
    public Map<String, Object> getCurrentUser() {
        SysUser sysUser = sysUserMapper.selectById(LoginUserInfoHelper.getUserId());
        Map<String, Object> map = new HashMap<>();
        map.put("name", sysUser.getName());
        map.put("phone", sysUser.getPhone());
        return map;
    }

    /**
     * 删除用户（同时删除部门用户关联）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        // 删除部门用户关联记录
        sysDeptUserService.deleteByUserId((Long) id);
        // 删除用户
        return SysUserServiceImpl.super.removeById(id);
    }

    /**
     * 分页查询用户信息，包含部门名称和角色名称
     */
    @Override
    public IPage<SysUserVo> findPageWithDeptAndRole(Long page, Long limit, SysUserQueryVo sysUserQueryVo) {
        // 创建分页对象
        Page<SysUser> userPageParam = new Page<>(page, limit);

        // 封装查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        String keyword = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();
        queryWrapper.like(StringUtils.hasLength(keyword), SysUser::getName, keyword);
        queryWrapper.gt(StringUtils.hasLength(createTimeBegin), SysUser::getCreateTime, createTimeBegin);
        queryWrapper.le(StringUtils.hasLength(createTimeEnd), SysUser::getCreateTime, createTimeEnd);

        // 实现分页查询
        Page<SysUser> userPage = this.page(userPageParam, queryWrapper);
        List<SysUser> userList = userPage.getRecords();

        if (userList == null || userList.isEmpty()) {
            // 空结果直接返回
            Page<SysUserVo> resultPage = new Page<>(page, limit);
            resultPage.setTotal(0);
            resultPage.setRecords(Collections.emptyList());
            return resultPage;
        }

        // 获取所有用户ID
        List<Long> userIdList = userList.stream().map(SysUser::getId).collect(Collectors.toList());

        // 1. 查询用户-角色关联信息（sys_user_role INNER JOIN sys_role）
        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.in(SysUserRole::getUserId, userIdList);
        List<SysUserRole> userRoleList = sysUserRoleMapper.selectList(userRoleWrapper);

        // 获取所有角色ID
        List<Long> roleIdList = userRoleList.stream()
                .map(SysUserRole::getRoleId)
                .distinct()
                .collect(Collectors.toList());

        // 查询角色信息
        Map<Long, String> roleNameMap = new HashMap<>();
        if (roleIdList != null && !roleIdList.isEmpty()) {
            List<SysRole> roleList = sysRoleService.listByIds(roleIdList);
            roleNameMap = roleList.stream()
                    .collect(Collectors.toMap(SysRole::getId, SysRole::getRoleName, (v1, v2) -> v1));
        }

        // 2. 查询用户-部门关联信息（sys_dept_user INNER JOIN sys_dept）
        LambdaQueryWrapper<SysDeptUser> deptUserWrapper = new LambdaQueryWrapper<>();
        deptUserWrapper.in(SysDeptUser::getUserId, userIdList);
        List<SysDeptUser> deptUserList = sysDeptUserMapper.selectList(deptUserWrapper);

        // 获取所有部门ID
        List<Long> deptIdList = deptUserList.stream()
                .map(SysDeptUser::getDeptId)
                .distinct()
                .collect(Collectors.toList());

        // 查询部门信息
        Map<Long, String> deptNameMap = new HashMap<>();
        if (deptIdList != null && !deptIdList.isEmpty()) {
            List<SysDept> deptList = sysDeptService.listByIds(deptIdList);
            deptNameMap = deptList.stream()
                    .collect(Collectors.toMap(SysDept::getId, SysDept::getName, (v1, v2) -> v1));
        }

        // 3. 构建用户ID到角色名称的映射（一个用户可能有多个角色）
        Map<Long, String> userRoleNameMap = new HashMap<>();
        for (SysUserRole userRole : userRoleList) {
            Long userId = userRole.getUserId();
            String roleName = roleNameMap.get(userRole.getRoleId());
            if (roleName != null) {
                String existingRoleNames = userRoleNameMap.getOrDefault(userId, "");
                if (StringUtils.hasLength(existingRoleNames)) {
                    userRoleNameMap.put(userId, existingRoleNames + "," + roleName);
                } else {
                    userRoleNameMap.put(userId, roleName);
                }
            }
        }

        // 4. 构建用户ID到部门名称的映射
        Map<Long, String> userDeptNameMap = new HashMap<>();
        for (SysDeptUser deptUser : deptUserList) {
            Long userId = deptUser.getUserId();
            String deptName = deptNameMap.get(deptUser.getDeptId());
            if (deptName != null) {
                userDeptNameMap.put(userId, deptName);
            }
        }

        // 5. 转换为VO并填充部门名称和角色名称
        List<SysUserVo> voList = userList.stream().map(user -> {
            SysUserVo vo = new SysUserVo();
            // 复制父类属性
            org.springframework.beans.BeanUtils.copyProperties(user, vo);
            // 设置部门名称
            vo.setDeptName(userDeptNameMap.get(user.getId()));
            // 设置角色名称
            vo.setRoleName(userRoleNameMap.get(user.getId()));
            return vo;
        }).collect(Collectors.toList());

        // 6. 构建返回的分页结果
        Page<SysUserVo> resultPage = new Page<>(page, limit);
        resultPage.setTotal(userPage.getTotal());
        resultPage.setRecords(voList);
        return resultPage;
    }

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private SysDeptUserMapper sysDeptUserMapper;
}