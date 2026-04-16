package com.yunshang.process.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunshang.common.result.Result;
import com.yunshang.process.service.ProcessService;
import com.yunshang.vo.process.ProcessQueryVo;
import com.yunshang.vo.process.ProcessVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author nanfeng
 * @description 审批流管理
 * @date 2023-03-18 17:27
 */
@RestController
@RequestMapping(value = "/admin/process")
@SuppressWarnings({"rawtypes"})
public class ProcessController {

    @Resource
    private ProcessService processService;

    @PreAuthorize("hasAuthority('bnt.process.list')")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page, @PathVariable Long limit, ProcessQueryVo processQueryVo) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = processService.selectPage(pageParam, processQueryVo);
        return Result.ok(pageModel);
    }
}
