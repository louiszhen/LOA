package com.yunshang.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yunshang.model.system.SysUser;
import com.yunshang.vo.system.SysUserQueryVo;
import com.yunshang.vo.system.SysUserVo;

import java.util.Map;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-07 21:22
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 修改用户状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 通过用户名获取用户对象
     */
    SysUser getByUsername(String username);

    /**
     * 根据用户名获取用户登录信息
     */
    Map<String, Object> getUserInfo(String username);

    /**
     * 获取当前用户基本信息
     */
    Map<String, Object> getCurrentUser();

    /**
     * 分页查询用户信息，包含部门名称和角色名称
     *
     * @param page            页码
     * @param limit           每页数量
     * @param sysUserQueryVo  查询条件
     * @return 分页结果
     */
    IPage<SysUserVo> findPageWithDeptAndRole(Long page, Long limit, SysUserQueryVo sysUserQueryVo);
}
