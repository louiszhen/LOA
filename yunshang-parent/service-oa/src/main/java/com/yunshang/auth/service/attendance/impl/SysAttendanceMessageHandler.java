package com.yunshang.auth.service.attendance.impl;

import com.yunshang.auth.mapper.attendance.SysAttendanceRecordMapper;
import com.yunshang.model.attendance.SysAttendanceRecord;
import com.yunshang.vo.attendance.AttendanceMessageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 考勤消息处理器（批量落盘版本）
 *
 * 核心逻辑：
 *   批量 INSERT 打卡明细（一次 IO 插多条）
 *   统计字段由每日 22:00 定时任务按六维规则统一结算，不在此处更新。
 *
 * @author louis
 * @date 2026-03-20
 */
@Slf4j
@Service
public class SysAttendanceMessageHandler {

    @Autowired
    private SysAttendanceRecordMapper recordMapper;

    /**
     * 批量处理入口：仅保存打卡明细，统计由每日22:00定时任务统一结算
     *
     * @param messages 本次刷盘的消息列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void processBatch(List<AttendanceMessageVo> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        batchInsertRecords(messages);
    }

    // -------------------------------------------------------------------------
    // 打卡明细批量插入
    // -------------------------------------------------------------------------

    private void batchInsertRecords(List<AttendanceMessageVo> messages) {
        Date now = new Date();
        List<SysAttendanceRecord> records = messages.stream().map(msg -> {
            SysAttendanceRecord r = new SysAttendanceRecord();
            r.setUserId(msg.getUserId());
            r.setUserName(msg.getUserName());
            r.setClockDate(msg.getClockDate());
            r.setClockType(msg.getClockType());
            r.setClockTime(msg.getClockTime());
            r.setStatus(msg.getStatus());
            r.setStatusDesc(msg.getStatusDesc());
            r.setRemark(msg.getRemark());
            r.setCreateTime(now);
            r.setUpdateTime(now);
            r.setIsDeleted(0);
            return r;
        }).collect(Collectors.toList());

        recordMapper.batchInsert(records);
        log.debug("批量插入打卡明细 {} 条", records.size());
    }

}
