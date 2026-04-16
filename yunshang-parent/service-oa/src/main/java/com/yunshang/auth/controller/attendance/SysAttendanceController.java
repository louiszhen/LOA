package com.yunshang.auth.controller.attendance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yunshang.auth.service.attendance.SysAttendanceService;
import com.yunshang.common.result.Result;
import com.yunshang.model.attendance.SysAttendanceRecord;
import com.yunshang.model.attendance.SysAttendanceStatistics;
import com.yunshang.security.custom.LoginUserInfoHelper;
import com.yunshang.vo.attendance.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 考勤控制器
 * @author louis
 * @date 2026-03-20
 */
@Api(tags = "考勤管理")
@RestController
@RequestMapping("/admin/attendance")
@Slf4j
public class SysAttendanceController {

    @Autowired
    private SysAttendanceService attendanceService;

    /**
     * 打卡
     */
    @ApiOperation(value = "打卡")
    @PostMapping("/clock")
    public Result<ClockResponseVo> clock(@RequestBody ClockRequestVo requestVo) {
        ClockResponseVo response = attendanceService.clock(requestVo);
        if (response.getSuccess()) {
            return Result.ok(response);
        } else {
            return Result.<ClockResponseVo>fail().message(response.getMessage());
        }
    }

    /**
     * 查询打卡记录列表
     */
    @ApiOperation(value = "查询打卡记录列表")
    @GetMapping("/record/list")
    public Result<List<SysAttendanceRecord>> getRecordList(
            @ApiParam(value = "开始日期", example = "2026-03-01") 
            @RequestParam(required = false) String startDate,
            @ApiParam(value = "结束日期", example = "2026-03-20") 
            @RequestParam(required = false) String endDate,
            @ApiParam(value = "打卡类型", example = "morning") 
            @RequestParam(required = false) String clockType,
            @ApiParam(value = "打卡状态", example = "normal") 
            @RequestParam(required = false) String status,
            @ApiParam(value = "用户ID") 
            @RequestParam(required = false) Long userId,
            @ApiParam(value = "用户姓名") 
            @RequestParam(required = false) String userName) {
        
        AttendanceRecordQueryVo queryVo = new AttendanceRecordQueryVo();
        queryVo.setStartDate(startDate);
        queryVo.setEndDate(endDate);
        queryVo.setClockType(clockType);
        queryVo.setStatus(status);
        queryVo.setUserId(userId);
        queryVo.setUserName(userName);
        
        List<SysAttendanceRecord> list = attendanceService.selectRecordList(queryVo);
        return Result.ok(list);
    }

    /**
     * 分页查询打卡记录列表
     */
    @ApiOperation(value = "分页查询打卡记录列表")
    @GetMapping("/record/list/{current}/{size}")
    public Result<IPage<SysAttendanceRecord>> getRecordPage(
            @ApiParam(value = "当前页", example = "1") @PathVariable long current,
            @ApiParam(value = "每页条数", example = "10") @PathVariable long size,
            @ApiParam(value = "开始日期", example = "2026-03-01")
            @RequestParam(required = false) String startDate,
            @ApiParam(value = "结束日期", example = "2026-03-31")
            @RequestParam(required = false) String endDate,
            @ApiParam(value = "打卡类型", example = "morning")
            @RequestParam(required = false) String clockType,
            @ApiParam(value = "打卡状态", example = "normal")
            @RequestParam(required = false) String status,
            @ApiParam(value = "用户ID")
            @RequestParam(required = false) Long userId,
            @ApiParam(value = "用户姓名")
            @RequestParam(required = false) String userName) {

        AttendanceRecordQueryVo queryVo = new AttendanceRecordQueryVo();
        queryVo.setStartDate(startDate);
        queryVo.setEndDate(endDate);
        queryVo.setClockType(clockType);
        queryVo.setStatus(status);
        queryVo.setUserId(userId);
        queryVo.setUserName(userName);

        IPage<SysAttendanceRecord> page = attendanceService.selectRecordPage(current, size, queryVo);
        return Result.ok(page);
    }

    /**
     * 查询考勤统计列表
     */
    @ApiOperation(value = "查询考勤统计列表")
    @GetMapping("/statistics/list")
    public Result<List<SysAttendanceStatistics>> getStatisticsList(
            @ApiParam(value = "统计月份", example = "202603") 
            @RequestParam(required = false) String statMonth,
            @ApiParam(value = "用户ID") 
            @RequestParam(required = false) Long userId,
            @ApiParam(value = "用户姓名") 
            @RequestParam(required = false) String userName) {
        
        AttendanceStatisticsQueryVo queryVo = new AttendanceStatisticsQueryVo();
        queryVo.setStatMonth(statMonth);
        queryVo.setUserId(userId);
        queryVo.setUserName(userName);
        
        List<SysAttendanceStatistics> list = attendanceService.selectStatisticsList(queryVo);
        return Result.ok(list);
    }

    /**
     * 分页查询考勤统计列表
     */
    @ApiOperation(value = "分页查询考勤统计列表")
    @GetMapping("/statistics/list/{current}/{size}")
    public Result<IPage<SysAttendanceStatistics>> getStatisticsPage(
            @ApiParam(value = "当前页", example = "1") @PathVariable long current,
            @ApiParam(value = "每页条数", example = "10") @PathVariable long size,
            @ApiParam(value = "统计月份", example = "202603")
            @RequestParam(required = false) String statMonth,
            @ApiParam(value = "用户ID")
            @RequestParam(required = false) Long userId,
            @ApiParam(value = "用户姓名")
            @RequestParam(required = false) String userName) {

        AttendanceStatisticsQueryVo queryVo = new AttendanceStatisticsQueryVo();
        queryVo.setStatMonth(statMonth);
        queryVo.setUserId(userId);
        queryVo.setUserName(userName);

        IPage<SysAttendanceStatistics> page = attendanceService.selectStatisticsPage(current, size, queryVo);
        return Result.ok(page);
    }

    /**
     * 获取当前用户当月考勤统计
     */
    @ApiOperation(value = "获取当前用户当月考勤统计")
    @GetMapping("/statistics/current/user")
    public Result<SysAttendanceStatistics> getCurrentUserMonthlyStatistics() {
        Long userId = LoginUserInfoHelper.getUserId();
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        SysAttendanceStatistics statistics = attendanceService.getUserMonthlyStatistics(userId, currentMonth);
        return Result.ok(statistics);
    }

    /**
     * 获取指定用户指定月份考勤统计
     */
    @ApiOperation(value = "获取指定用户指定月份考勤统计")
    @GetMapping("/statistics/{userId}/{statMonth}")
    public Result<SysAttendanceStatistics> getUserMonthlyStatistics(
            @ApiParam(value = "用户ID") @PathVariable Long userId,
            @ApiParam(value = "统计月份", example = "202603") @PathVariable String statMonth) {
        SysAttendanceStatistics statistics = attendanceService.getUserMonthlyStatistics(userId, statMonth);
        return Result.ok(statistics);
    }

    /**
     * 检查当前用户今日是否已打卡
     */
    @ApiOperation(value = "检查当前用户今日是否已打卡")
    @GetMapping("/check/today")
    public Result<Object> checkTodayClockStatus(
            @ApiParam(value = "打卡类型", example = "morning") 
            @RequestParam(defaultValue = "morning") String clockType) {
        
        Long userId = LoginUserInfoHelper.getUserId();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        boolean morningClocked = attendanceService.isAlreadyClocked(userId, today, "morning");
        boolean eveningClocked = attendanceService.isAlreadyClocked(userId, today, "evening");
        
        return Result.ok(java.util.Map.of(
            "date", today,
            "morningClocked", morningClocked,
            "eveningClocked", eveningClocked
        ));
    }

    /**
     * 获取当前用户指定日期打卡状态
     */
    @ApiOperation(value = "获取当前用户指定日期打卡状态")
    @GetMapping("/clockStatus")
    public Result<java.util.Map<String, Object>> getClockStatus(
            @ApiParam(value = "打卡日期", example = "2026-03-21") 
            @RequestParam(required = false) String clockDate) {
        
        if (clockDate == null || clockDate.isEmpty()) {
            clockDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        
        java.util.Map<String, Boolean> statusMap = attendanceService.getTodayClockStatus(clockDate);
        
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("date", clockDate);
        result.putAll(statusMap);
        
        return Result.ok(result);
    }

    /**
     * 获取当前用户最近三天打卡记录
     */
    @ApiOperation(value = "获取当前用户最近三天打卡记录")
    @GetMapping("/record/latest")
    public Result<List<SysAttendanceRecord>> getLatestRecord() {
        List<SysAttendanceRecord> records = attendanceService.getLatestRecord();
        return Result.ok(records);
    }
}
