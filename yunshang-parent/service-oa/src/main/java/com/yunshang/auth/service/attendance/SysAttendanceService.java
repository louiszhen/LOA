package com.yunshang.auth.service.attendance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yunshang.model.attendance.SysAttendanceRecord;
import com.yunshang.model.attendance.SysAttendanceStatistics;
import com.yunshang.vo.attendance.*;

import java.util.List;

/**
 * 考勤服务接口
 * @author louis
 * @date 2026-03-20
 */
public interface SysAttendanceService {

    /**
     * 打卡
     * @param requestVo 打卡请求
     * @return 打卡结果
     */
    ClockResponseVo clock(ClockRequestVo requestVo);

    /**
     * 查询打卡记录列表
     * @param queryVo 查询条件
     * @return 打卡记录列表
     */
    List<SysAttendanceRecord> selectRecordList(AttendanceRecordQueryVo queryVo);

    /**
     * 分页查询打卡记录列表
     * @param current 当前页
     * @param size    每页条数
     * @param queryVo 查询条件
     * @return 分页打卡记录列表
     */
    IPage<SysAttendanceRecord> selectRecordPage(long current, long size, AttendanceRecordQueryVo queryVo);

    /**
     * 分页查询考勤统计列表
     * @param current 当前页
     * @param size    每页条数
     * @param queryVo 查询条件
     * @return 分页统计列表
     */
    IPage<SysAttendanceStatistics> selectStatisticsPage(long current, long size, AttendanceStatisticsQueryVo queryVo);

    /**
     * 查询考勤统计列表
     * @param queryVo 查询条件
     * @return 统计列表
     */
    List<SysAttendanceStatistics> selectStatisticsList(AttendanceStatisticsQueryVo queryVo);

    /**
     * 获取用户当月考勤统计
     * @param userId 用户ID
     * @param statMonth 统计月份
     * @return 统计信息
     */
    SysAttendanceStatistics getUserMonthlyStatistics(Long userId, String statMonth);

    /**
     * 检查用户是否已打卡
     * @param userId 用户ID
     * @param clockDate 打卡日期
     * @param clockType 打卡类型
     * @return 是否已打卡
     */
    boolean isAlreadyClocked(Long userId, String clockDate, String clockType);

    /**
     * 从Redis获取用户打卡状态
     * @param userId 用户ID
     * @param clockDate 打卡日期
     * @param clockType 打卡类型
     * @return 是否已打卡
     */
    boolean getClockStatusFromRedis(Long userId, String clockDate, String clockType);

    /**
     * 获取当前用户今日打卡状态
     * @param clockDate 打卡日期（格式：yyyy-MM-dd）
     * @return 打卡状态Map，包含 morningClocked 和 eveningClocked
     */
    java.util.Map<String, Boolean> getTodayClockStatus(String clockDate);

    /**
     * 获取当前用户最近三天打卡记录
     * @return 最近三天打卡记录列表
     */
    List<SysAttendanceRecord> getLatestRecord();
}
