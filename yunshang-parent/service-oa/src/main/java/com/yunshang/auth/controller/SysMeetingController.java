package com.yunshang.auth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunshang.auth.service.SysMeetingService;
import com.yunshang.common.result.Result;
import com.yunshang.model.system.SysMeeting;
import com.yunshang.vo.system.SysMeetingQueryVo;
import com.yunshang.vo.system.SysMeetingVo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sysMeeting")
public class SysMeetingController {

    @Resource
    private SysMeetingService sysMeetingService;

    //判断当前会议是否存在时间重叠
    @RequestMapping("/checkMeeting")
    public Result checkMeeting(@RequestBody SysMeeting sysMeeting) {
        boolean isAvailable = sysMeetingService.checkMeeting(sysMeeting);
        // 有冲突返回错误，无冲突返回成功
        if (!isAvailable) {
            return Result.fail("会议时间存在交叉冲突");
        }
        return Result.ok("当前会议设置时间可用");
    }

    @RequestMapping("/addMeeting")
    public Result addMeeting(@RequestBody SysMeeting sysMeeting) {
        sysMeetingService.addMeeting(sysMeeting);
        return Result.ok("会议添加成功");
    }

    @RequestMapping("/updateMeeting")
    public Result updateMeeting(@RequestBody SysMeeting sysMeeting) {
        sysMeetingService.updateMeeting(sysMeeting);
        return Result.ok("会议修改成功");
    }

    @RequestMapping("/removeMeeting")
    public Result removeMeeting(Long id) {
        sysMeetingService.removeMeeting(id);
        return Result.ok("会议删除成功");
    }

    @RequestMapping("/findByUserId/{page}/{limit}")
    public Result findByUserId(@PathVariable Long page, @PathVariable Long limit) {
        Page<SysMeeting> pageParam = new Page<>(page, limit);
        Page<SysMeetingVo> meetingPage = sysMeetingService.findByUserId(pageParam);
        return Result.ok(meetingPage);
    }

    @RequestMapping("/findAll")
    public Result findAll() {
        List<SysMeetingVo> meetingList = sysMeetingService.findAll();
        return Result.ok(meetingList);
    }

    @RequestMapping("/findAll/{page}/{limit}")
    public Result findAllByPage(@PathVariable Long page, @PathVariable Long limit) {
        Page<SysMeeting> pageParam = new Page<>(page, limit);
        Page<SysMeetingVo> meetingPage = sysMeetingService.findAllByPage(pageParam);
        return Result.ok(meetingPage);
    }

    @RequestMapping("/search/{page}/{limit}")
    public Result search(@PathVariable Long page, @PathVariable Long limit,
                         @RequestBody(required = false) SysMeetingQueryVo queryVo) {
        Page<SysMeeting> pageParam = new Page<>(page, limit);
        Page<SysMeetingVo> meetingPage = sysMeetingService.search(pageParam, queryVo);
        return Result.ok(meetingPage);
    }
}
