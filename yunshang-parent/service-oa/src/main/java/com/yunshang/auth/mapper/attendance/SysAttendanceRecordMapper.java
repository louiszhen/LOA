package com.yunshang.auth.mapper.attendance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yunshang.model.attendance.SysAttendanceRecord;
import com.yunshang.vo.attendance.AttendanceRecordQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 打卡明细Mapper
 * @author louis
 * @date 2026-03-20
 */
@Mapper
public interface SysAttendanceRecordMapper extends BaseMapper<SysAttendanceRecord> {

    /**
     * 查询打卡记录列表
     */
    List<SysAttendanceRecord> selectRecordList(@Param("queryVo") AttendanceRecordQueryVo queryVo);

    /**
     * 分页查询打卡记录列表
     * 第一个参数为 IPage，MyBatis-Plus 分页拦截器自动处理 COUNT + LIMIT
     */
    IPage<SysAttendanceRecord> selectRecordPage(IPage<SysAttendanceRecord> page,
                                               @Param("queryVo") AttendanceRecordQueryVo queryVo);

    /**
     * 检查是否已打卡
     */
    Integer checkAlreadyClocked(@Param("userId") Long userId,
                                @Param("clockDate") String clockDate,
                                @Param("clockType") String clockType);

    /**
     * 批量插入打卡明细记录
     *
     * @param records 明细列表
     */
    void batchInsert(@Param("records") List<SysAttendanceRecord> records);
}
