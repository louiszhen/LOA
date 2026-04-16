package com.yunshang.auth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yunshang.model.system.SysMeetingRoom;

public interface MeetingRoomService extends IService<SysMeetingRoom> {

    void saveMeetingRoom(SysMeetingRoom sysMeetingRoom);

    Page<SysMeetingRoom> pageQueryMeetingRoom(Page<SysMeetingRoom> pageParam, String roomNumber);
}
