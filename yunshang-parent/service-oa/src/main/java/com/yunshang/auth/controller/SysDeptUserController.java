package com.yunshang.auth.controller;

import com.yunshang.auth.service.SysDeptUserService;
import com.yunshang.common.result.Result;
import com.yunshang.model.system.SysDeptUser;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author louiszhen
 * @description 部门用户关联Controller
 * @date 2026-03-19
 */
@RestController
@RequestMapping("/admin/system/sysDeptUser")
@SuppressWarnings("rawtypes")
public class SysDeptUserController {

    @Resource
    private SysDeptUserService sysDeptUserService;

    /**
     * 创建用户部门关联记录
     */
    @PostMapping("save")
    public Result save(@RequestBody SysDeptUser sysDeptUser) {
        sysDeptUserService.save(sysDeptUser);
        return Result.ok();
    }

    /**
     * 删除用户部门关联记录
     */
    @DeleteMapping("remove/{userId}")
    public Result remove(@PathVariable Long userId) {
        sysDeptUserService.deleteByUserId(userId);
        return Result.ok();
    }

    /**
     * 根据部门id查询部门用户关联记录
     */
    @GetMapping("findByDeptId/{deptId}")
    public Result findByDeptId(@PathVariable Long deptId) {
        List<SysDeptUser> list = sysDeptUserService.findByDeptId(deptId);
        return Result.ok(list);
    }
}
