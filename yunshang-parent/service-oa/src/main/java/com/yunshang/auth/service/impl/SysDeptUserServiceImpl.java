package com.yunshang.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunshang.auth.mapper.SysDeptUserMapper;
import com.yunshang.auth.service.SysDeptUserService;
import com.yunshang.model.system.SysDeptUser;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author louiszhen
 * @description 部门用户关联Service实现
 * @date 2026-03-19
 */
@Service
public class SysDeptUserServiceImpl extends ServiceImpl<SysDeptUserMapper, SysDeptUser> implements SysDeptUserService {

    /**
     * 根据部门id查询部门用户关联记录
     */
    @Override
    public List<SysDeptUser> findByDeptId(Long deptId) {
        LambdaQueryWrapper<SysDeptUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDeptUser::getDeptId, deptId);
        return this.list(queryWrapper);
    }

    /**
     * 删除部门用户关联记录
     */
    @Override
    public void deleteByDeptId(Long deptId) {
        LambdaQueryWrapper<SysDeptUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDeptUser::getDeptId, deptId);
        this.remove(queryWrapper);
    }

    /**
     * 删除用户部门关联记录
     */
    @Override
    public void deleteByUserId(Long userId) {
        LambdaQueryWrapper<SysDeptUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDeptUser::getUserId, userId);
        this.remove(queryWrapper);
    }
}
