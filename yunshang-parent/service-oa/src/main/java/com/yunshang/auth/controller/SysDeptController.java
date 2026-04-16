package com.yunshang.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunshang.auth.service.SysDeptService;
import com.yunshang.common.result.Result;
import com.yunshang.model.system.SysDept;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author louiszhen
 * @description 部门管理Controller
 * @date 2026-03-19
 */
@RestController
@RequestMapping("/admin/system/sysDept")
@SuppressWarnings("rawtypes")
public class SysDeptController {

    @Resource
    private SysDeptService sysDeptService;

    /**
     * 部门分页条件查询
     */
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        SysDept sysDeptQuery) {
        // 创建page对象
        Page<SysDept> pageParam = new Page<>();

        // 封装查询条件
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
        String keyword = sysDeptQuery.getName();
        queryWrapper.like(StringUtils.hasLength(keyword), SysDept::getName, keyword);

        // 实现分页查询
        Page<SysDept> deptPage = sysDeptService.page(pageParam, queryWrapper);
        return Result.ok(deptPage);
    }

    /**
     * 根据id获取部门
     */
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        SysDept dept = sysDeptService.getById(id);
        return Result.ok(dept);
    }

    /**
     * 保存部门
     */
    @PostMapping("save")
    public Result save(@RequestBody SysDept dept) {
        sysDeptService.save(dept);
        return Result.ok(dept);
    }

    /**
     * 更新部门
     */
    @PutMapping("update")
    public Result updateById(@RequestBody SysDept dept) {
        sysDeptService.updateById(dept);
        return Result.ok();
    }

    /**
     * 删除部门
     */
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        sysDeptService.removeById(id);
        return Result.ok();
    }
}
