package com.yunshang.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunshang.auth.service.SysUserService;
import com.yunshang.common.result.Result;
import com.yunshang.model.system.SysUser;
import com.yunshang.vo.system.SysUserQueryVo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-08 19:14
 */
@RestController
@RequestMapping("/admin/system/sysUser")
@SuppressWarnings("rawtypes")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 用户分页条件查询
     */
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        SysUserQueryVo sysUserQueryVo) {
        // 创建page对象
        Page<SysUser> userPageParam = new Page<>();

        // 封装查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        String keyword = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();
        queryWrapper.like(StringUtils.hasLength(keyword), SysUser::getName, keyword);
        queryWrapper.gt(StringUtils.hasLength(createTimeBegin), SysUser::getCreateTime, createTimeBegin);
        queryWrapper.le(StringUtils.hasLength(createTimeEnd), SysUser::getCreateTime, createTimeEnd);

        // 实现分页查询
        Page<SysUser> userPage = sysUserService.page(userPageParam, queryWrapper);
        return Result.ok(userPage);
    }

    /**
     * 用户分页条件查询（包含部门名称和角色名称）
     */
    @GetMapping("findPage/{page}/{limit}")
    public Result findPage(@PathVariable Long page,
                           @PathVariable Long limit,
                           SysUserQueryVo sysUserQueryVo) {
        return Result.ok(sysUserService.findPageWithDeptAndRole(page, limit, sysUserQueryVo));
    }

    /**
     * 根据id获取用户
     */
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        return Result.ok(user);
    }

    /**
     * 保存用户
     */
    @PostMapping("save")
    public Result save(@RequestBody SysUser user) {
        sysUserService.save(user);
        return Result.ok();
    }

    /**
     * 更新用户
     */
    @PutMapping("update")
    public Result updateById(@RequestBody SysUser user) {
        sysUserService.updateById(user);
        return Result.ok();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.ok();
    }

    /**
     * 修改用户状态，当用户状态为正常时，可以访问后台系统，当用户状态停用后，不可以登录后台系统
     * 1-->正常
     * 0-->停用
     */
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        sysUserService.updateStatus(id, status);
        return Result.ok();
    }

    /**
     * 获取当前用户基本信息
     */
    @GetMapping("getCurrentUser")
    public Result getCurrentUser() {
        return Result.ok(sysUserService.getCurrentUser());
    }
}
