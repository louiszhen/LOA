package com.yunshang.process.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunshang.common.result.Result;
import com.yunshang.model.process.ProcessType;
import com.yunshang.process.service.ProcessTypeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author nanfeng
 * @description 审批类型
 * @date 2023-03-17 20:46
 */
@RestController
@RequestMapping(value = "/admin/process/processType")
@SuppressWarnings({"rawtypes"})
public class ProcessTypeController {

    @Resource
    private ProcessTypeService processTypeService;

    @PreAuthorize("hasAuthority('bnt.processType.list')")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit) {
        Page<ProcessType> pageParam = new Page<>(page, limit);
        IPage<ProcessType> pageModel = processTypeService.page(pageParam);
        return Result.ok(pageModel);
    }

    @PreAuthorize("hasAuthority('bnt.processType.list')")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessType processType = processTypeService.getById(id);
        return Result.ok(processType);
    }

    @PreAuthorize("hasAuthority('bnt.processType.add')")
    @PostMapping("save")
    public Result save(@RequestBody ProcessType processType) {
        processTypeService.save(processType);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.processType.update')")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessType processType) {
        processTypeService.updateById(processType);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.processType.remove')")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        processTypeService.removeById(id);
        return Result.ok();
    }

    @GetMapping("findAll")
    public Result findAll() {
        return Result.ok(processTypeService.list());
    }
}
