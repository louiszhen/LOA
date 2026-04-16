package com.yunshang.auth.mapper.attendance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yunshang.model.attendance.SysAttendanceStatistics;
import com.yunshang.vo.attendance.AttendanceStatisticsQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 打卡统计Mapper
 * @author louis
 * @date 2026-03-20
 */
@Mapper
public interface SysAttendanceStatisticsMapper extends BaseMapper<SysAttendanceStatistics> {

    /**
     * 分页查询统计列表
     * 第一个参数为 IPage，MyBatis-Plus 分页拦截器自动处理 COUNT + LIMIT
     */
    IPage<SysAttendanceStatistics> selectStatisticsPage(IPage<SysAttendanceStatistics> page,
                                                        @Param("queryVo") AttendanceStatisticsQueryVo queryVo);

    /**
     * 查询统计列表
     */
    List<SysAttendanceStatistics> selectStatisticsList(@Param("queryVo") AttendanceStatisticsQueryVo queryVo);

    /**
     * 根据用户ID和月份查询统计
     */
    SysAttendanceStatistics selectByUserAndMonth(@Param("userId") Long userId,
                                                @Param("statMonth") String statMonth);

    /**
     * 批量 UPSERT 统计记录。
     *
     * 使用 INSERT ... ON DUPLICATE KEY UPDATE 语法，传入的是本次小批量的差量就行，
     * MySQL 内部将差量累加到现有记录上，天然解决并发插入冲突。
     *
     * @param deltaList 差量列表（已经内存聚合，每个元素对应一个 userId+statMonth）
     */
    void batchUpsert(@Param("list") List<SysAttendanceStatistics> deltaList);
}
