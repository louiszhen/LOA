package com.yunshang.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunshang.model.system.SysDeptUser;

import java.util.List;

/**
 * @author louiszhen
 * @description 部门用户关联Service接口
 * @date 2026-03-19
 */
public interface SysDeptUserService extends IService<SysDeptUser> {

    /**
     * 根据部门id查询部门用户关联记录
     */
    List<SysDeptUser> findByDeptId(Long deptId);

    /**
     * 删除部门用户关联记录
     */
    void deleteByDeptId(Long deptId);

    /**
     * 删除用户部门关联记录
     */
    void deleteByUserId(Long userId);
}
