package com.yunshang.process.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunshang.common.result.Result;
import com.yunshang.model.process.Process;
import com.yunshang.model.process.ProcessTemplate;
import com.yunshang.process.service.ProcessService;
import com.yunshang.process.service.ProcessTemplateService;
import com.yunshang.process.service.ProcessTypeService;
import com.yunshang.vo.process.ApprovalVo;
import com.yunshang.vo.process.ProcessFormVo;
import com.yunshang.vo.process.ProcessVo;
//import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author nanfeng
 * @description 审批流管理
 * @date 2023-03-18 17:59
 */
@RestController
@RequestMapping(value = "/admin/process")
@CrossOrigin  // 跨域
@SuppressWarnings("rawtypes")
public class ProcessApiController {

    @Resource
    private ProcessTypeService processTypeService;

    @Resource
    private ProcessTemplateService processTemplateService;

    @Resource
    private ProcessService processService;

    /**
     * 获取全部审批分类及模版
     */
    @GetMapping("findProcessType")
    public Result findProcessType() {
        return Result.ok(processTypeService.findProcessType());
    }

    /**
     * 获取全部审批类型及已发布的模版（用于发起申请页面）
     */
    @GetMapping("findPublishedProcessType")
    public Result findPublishedProcessType() {
        return Result.ok(processTypeService.findPublishedProcessTypeWithTemplates());
    }

    /**
     * 获取审批模版
     */
    @GetMapping("getProcessTemplate/{processTemplateId}")
    public Result get(@PathVariable Long processTemplateId) {
        ProcessTemplate processTemplate = processTemplateService.getById(processTemplateId);
        return Result.ok(processTemplate);
    }

    /**
     * 启动审批流程实例
     */
    @PostMapping("/startUp")
    public Result start(@RequestBody ProcessFormVo processFormVo) {
        processService.startUp(processFormVo);
        return Result.ok();
    }

    /**
     * 查询待处理任务
     */
    @GetMapping("/findPending/{page}/{limit}")
    public Result findPending(@PathVariable Long page, @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page, limit);
        return Result.ok(processService.findPending(pageParam));
    }

    /**
     * 获取审批详情
     */
    @GetMapping("show/{id}")
    public Result show(@PathVariable Long id) {
        return Result.ok(processService.show(id));
    }

    /**
     * 审批操作
     */
    @PostMapping("approve")
    public Result approve(@RequestBody ApprovalVo approvalVo) {
        processService.approve(approvalVo);
        return Result.ok();
    }

    /**
     * 查询已处理任务
     */
    @GetMapping("/findProcessed/{page}/{limit}")
    public Result findProcessed(@PathVariable Long page, @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page, limit);
        return Result.ok(processService.findProcessed(pageParam));
    }

    /**
     * 已发起
     */
    @GetMapping("/findStarted/{page}/{limit}")
    public Result findStarted(@PathVariable Long page, @PathVariable Long limit) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        return Result.ok(processService.findStarted(pageParam));
    }

    /**
     * 获取审批流程进度（BPMN XML + 各状态节点 ID）
     */
    @GetMapping("/flowProgress/{processId}")
    public Result getFlowProgress(@PathVariable Long processId) {
        return Result.ok(processService.getFlowProgress(processId));
    }
}
