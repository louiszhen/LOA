package com.yunshang.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunshang.auth.mapper.MeetingRoomMapper;
import com.yunshang.auth.service.MeetingRoomService;
import com.yunshang.common.exception.YunshangException;
import com.yunshang.common.result.ResultCodeEnum;
import com.yunshang.model.system.SysMeetingRoom;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomMapper, SysMeetingRoom> implements MeetingRoomService {

    @Override
    public void saveMeetingRoom(SysMeetingRoom sysMeetingRoom) {
        try {
            this.save(sysMeetingRoom);
        } catch (DuplicateKeyException e) {
            throw new YunshangException(ResultCodeEnum.MEETING_ROOM_EXIST);
        }
    }

    @Override
    public Page<SysMeetingRoom> pageQueryMeetingRoom(Page<SysMeetingRoom> pageParam, String roomNumber) {
        LambdaQueryWrapper<SysMeetingRoom> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(roomNumber)) {
            queryWrapper.like(SysMeetingRoom::getRoomNumber, roomNumber);
        }
        return this.page(pageParam, queryWrapper);
    }
}
