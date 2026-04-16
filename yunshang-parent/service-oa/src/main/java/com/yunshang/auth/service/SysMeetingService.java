package com.yunshang.auth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunshang.model.system.SysMeeting;
import com.yunshang.vo.system.SysMeetingQueryVo;
import com.yunshang.vo.system.SysMeetingVo;

import java.util.List;

public interface SysMeetingService {
    Boolean checkMeeting(SysMeeting sysMeeting);

    void addMeeting(SysMeeting sysMeeting);

    void updateMeeting(SysMeeting sysMeeting);

    void removeMeeting(Long id);

    Page<SysMeetingVo> findByUserId(Page<SysMeeting> pageParam);

    List<SysMeetingVo> findAll();

    Page<SysMeetingVo> findAllByPage(Page<SysMeeting> pageParam);

    Page<SysMeetingVo> search(Page<SysMeeting> pageParam, SysMeetingQueryVo queryVo);
}
