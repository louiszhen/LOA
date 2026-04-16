package com.yunshang.auth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunshang.auth.service.MeetingRoomService;
import com.yunshang.common.result.Result;
import com.yunshang.model.system.SysMeetingRoom;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/MeetingRoom")
public class MeetingRoomController {

    @Resource
    private MeetingRoomService meetingRoomService;

    @RequestMapping("/get")
    public Result getMeetingRoomAll(){
        return Result.ok(meetingRoomService.list());
    }

    @RequestMapping("/{page}/{limit}")
    public Result pageQueryMeetingRoom(@PathVariable Long page, @PathVariable Long limit, @RequestParam(required = false) String roomNumber){
        Page<SysMeetingRoom> pageParam = new Page<>(page, limit);
        Page<SysMeetingRoom> meetingRoomPage = meetingRoomService.pageQueryMeetingRoom(pageParam, roomNumber);
        return Result.ok(meetingRoomPage);
    }

    @RequestMapping("/get/{id}")
    public Result getMeetingRoomById(@PathVariable Long id){
        SysMeetingRoom sysMeetingRoom = meetingRoomService.getById(id);
        return Result.ok(sysMeetingRoom);
    }

    @RequestMapping("/save")
    public Result saveMeetingRoom(@RequestBody(required = false) SysMeetingRoom sysMeetingRoom){
        if (sysMeetingRoom == null) {
            return Result.fail("请求体不能为空");
        }
        meetingRoomService.saveMeetingRoom(sysMeetingRoom);
        return Result.ok();
    }

    @RequestMapping("/delete/{id}")
    public Result deleteMeetingRoom(@PathVariable Long id){
        meetingRoomService.removeById(id);
        return Result.ok();
    }

    @RequestMapping("/update")
    public Result updateMeetingRoom(@RequestBody SysMeetingRoom sysMeetingRoom){
        meetingRoomService.updateById(sysMeetingRoom);
        return Result.ok();
    }

}
