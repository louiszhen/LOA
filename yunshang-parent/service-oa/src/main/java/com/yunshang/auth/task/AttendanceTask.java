package com.yunshang.auth.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yunshang.auth.config.AttendanceConfig;
import com.yunshang.auth.mapper.attendance.SysAttendanceRecordMapper;
import com.yunshang.auth.mapper.attendance.SysAttendanceStatisticsMapper;
import com.yunshang.auth.mapper.SysUserMapper;
import com.yunshang.model.attendance.AttendanceStatus;
import com.yunshang.model.attendance.ClockType;
import com.yunshang.model.attendance.SysAttendanceRecord;
import com.yunshang.model.attendance.SysAttendanceStatistics;
import com.yunshang.model.system.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 考勤定时任务
 *
 * 六维统计规则（以天为粒度）：
 *   1. 早上迟到                     → late_days + 1
 *   2. 晚上早退                     → early_leave_days + 1
 *   3. 早晚都准时                   → normal_clock_days + 1
 *   4. 早上正常/迟到 + 晚上未打卡   → early_leave_days + 1
 *   5. 早上未打卡   + 晚上正常/早退 → late_days + 1
 *   6. 早晚都未打卡                 → absent_days + 1
 *
 * 执行时序：
 *   09:30 - checkMorningAbsence：补录早上未打卡记录（不更新统计）
 *   22:00 - checkEveningAndSettle：补录晚上未打卡记录 + 按六维规则结算当天统计
 *
 * @author louis
 * @date 2026-03-20
 */
@Slf4j
@Component
public class AttendanceTask {

    @Autowired
    private SysAttendanceRecordMapper recordMapper;

    @Autowired
    private SysAttendanceStatisticsMapper statisticsMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private RedissonClient redissonClient;

    // -------------------------------------------------------------------------
    // 09:30 - 补录早上未打卡（仅插入记录，不更新统计）
    // -------------------------------------------------------------------------

    /**
     * 每天09:30 检查上班打卡情况。
     * 未打卡则补录 absent 记录；统计字段不在此处更新，由22:00结算任务统一处理。
     */
    @Scheduled(cron = "0 30 9 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void checkMorningAbsence() {
        log.info("开始检查上班未打卡情况...");
        try {
            if (isWeekend()) {
                log.info("周末不检查上班打卡");
                return;
            }
            LocalDate today = LocalDate.now();
            String clockDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            for (SysUser user : getActiveUsers()) {
                if (!hasMorningRecord(user.getId(), clockDate)) {
                    insertAbsentMorningRecord(user, today, clockDate);
                }
            }
            log.info("上班未打卡检查完成");
        } catch (Exception e) {
            log.error("上班未打卡检查异常", e);
        }
    }

    // -------------------------------------------------------------------------
    // 22:00 - 补录晚上未打卡 + 按六维规则结算当天统计
    // -------------------------------------------------------------------------

    /**
     * 每天22:00 执行两步操作：
     *   Step1 - 对"早上有真实打卡、晚上未打卡"的用户补录 early_leave 记录
     *   Step2 - 对所有用户按六维规则结算当天考勤统计
     */
    @Scheduled(cron = "0 0 22 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void checkEveningAndSettle() {
        log.info("开始执行晚上未打卡检查 + 当天考勤结算...");
        try {
            LocalDate today = LocalDate.now();
            String clockDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String statMonth = today.format(DateTimeFormatter.ofPattern("yyyyMM"));

            List<SysUser> users = getActiveUsers();

            // Step1: 补录晚上未打卡记录
            // 仅针对早上有"真实打卡"（status != absent）的用户插入 early_leave
            // 早上也没打卡的用户不补录晚上记录，由结算逻辑判定为 absent
            for (SysUser user : users) {
                if (!hasEveningRecord(user.getId(), clockDate)
                        && hasMorningActualPunch(user.getId(), clockDate)) {
                    insertEarlyLeaveEveningRecord(user, today, clockDate);
                }
            }

            // Step2: 按六维规则结算当天所有用户的统计
            for (SysUser user : users) {
                settleDailyStatistics(user, clockDate, statMonth);
            }

            log.info("晚上未打卡检查 + 当天考勤结算完成");
        } catch (Exception e) {
            log.error("晚上考勤结算异常", e);
        }
    }

    // -------------------------------------------------------------------------
    // 六维规则结算
    // -------------------------------------------------------------------------

    /**
     * 按六维规则结算指定用户当天的考勤统计。
     *
     * 记录来源说明：
     *   - morning absent  = 09:30 系统补录（status=absent）或无记录
     *   - evening absent  = 无晚上记录（早上也未打卡时不补录晚上）
     *   - evening present = 用户真实打卡(normal/early_leave) 或 22:00 系统补录(early_leave)
     */
    private void settleDailyStatistics(SysUser user, String clockDate, String statMonth) {
        Long userId = user.getId();

        SysAttendanceRecord morningRec = getRecord(userId, clockDate, ClockType.MORNING.getCode());
        SysAttendanceRecord eveningRec = getRecord(userId, clockDate, ClockType.EVENING.getCode());

        String morningStatus = morningRec != null ? morningRec.getStatus() : null;
        String eveningStatus = eveningRec != null ? eveningRec.getStatus() : null;

        // 早上 absent：无记录 或 status=absent（系统补录）
        boolean morningAbsent = morningStatus == null
                || AttendanceStatus.ABSENT.getCode().equals(morningStatus);
        // 晚上 absent：无记录（早上未打卡时不补录晚上）
        boolean eveningAbsent = eveningStatus == null;

        int lateDelta = 0, earlyLeaveDelta = 0, normalDelta = 0, absentDelta = 0;

        if (morningAbsent && eveningAbsent) {
            // 规则6：早晚都未打卡
            absentDelta = 1;
        } else if (morningAbsent) {
            // 规则5：早上未打卡，晚上有打卡
            lateDelta = 1;
        } else if (eveningAbsent) {
            // 规则4：早上有打卡，晚上未打卡
            earlyLeaveDelta = 1;
        } else {
            // 早晚都有打卡，按各自状态独立判断
            if (AttendanceStatus.LATE.getCode().equals(morningStatus)) {
                lateDelta = 1;          // 规则1：早上迟到
            }
            if (AttendanceStatus.EARLY_LEAVE.getCode().equals(eveningStatus)) {
                earlyLeaveDelta = 1;    // 规则2：晚上早退
            }
            if (AttendanceStatus.NORMAL.getCode().equals(morningStatus)
                    && AttendanceStatus.NORMAL.getCode().equals(eveningStatus)) {
                normalDelta = 1;        // 规则3：早晚都准时
            }
        }

        updateStatistics(userId, user.getName(), statMonth,
                lateDelta, earlyLeaveDelta, normalDelta, absentDelta);

        log.debug("用户 {} 考勤结算：morning={}, evening={}, late={}, earlyLeave={}, normal={}, absent={}",
                userId, morningStatus, eveningStatus, lateDelta, earlyLeaveDelta, normalDelta, absentDelta);
    }

    // -------------------------------------------------------------------------
    // 统计更新（按差量字段）
    // -------------------------------------------------------------------------

    /**
     * 按差量更新统计记录（先查后改，无记录则新增）。
     * actualClockDays 每天结算时 +1（无论是否缺勤）。
     */
    private void updateStatistics(Long userId, String userName, String statMonth,
                                   int lateDelta, int earlyLeaveDelta, int normalDelta, int absentDelta) {
        SysAttendanceStatistics stat = statisticsMapper.selectByUserAndMonth(userId, statMonth);
        if (stat == null) {
            stat = new SysAttendanceStatistics();
            stat.setUserId(userId);
            stat.setUserName(userName);
            stat.setStatMonth(statMonth);
            stat.setShouldClockDays(0);
            stat.setActualClockDays(0);
            stat.setNormalClockDays(0);
            stat.setLateDays(0);
            stat.setEarlyLeaveDays(0);
            stat.setAbsentDays(0);
            stat.setCreateTime(new Date());
            stat.setIsDeleted(0);
        }

        stat.setActualClockDays(stat.getActualClockDays() + 1);
        stat.setLateDays(stat.getLateDays() + lateDelta);
        stat.setEarlyLeaveDays(stat.getEarlyLeaveDays() + earlyLeaveDelta);
        stat.setNormalClockDays(stat.getNormalClockDays() + normalDelta);
        stat.setAbsentDays(stat.getAbsentDays() + absentDelta);
        stat.setUpdateTime(new Date());

        if (stat.getId() == null) {
            statisticsMapper.insert(stat);
        } else {
            statisticsMapper.updateById(stat);
        }

        // 落库后删除对应的 Redis 缓存，保证首页下次读取到最新数据
        String cacheKey = AttendanceConfig.STATISTICS_CACHE_KEY_PREFIX + userId + ":" + statMonth;
        redissonClient.getBucket(cacheKey).delete();
        log.debug("考勤统计缓存已删除：userId={}, statMonth={}", userId, statMonth);
    }

    // -------------------------------------------------------------------------
    // 辅助方法
    // -------------------------------------------------------------------------

    /** 获取所有正常状态用户 */
    private List<SysUser> getActiveUsers() {
        LambdaQueryWrapper<SysUser> q = new LambdaQueryWrapper<>();
        q.eq(SysUser::getStatus, 1);
        return userMapper.selectList(q);
    }

    /** 早上是否有任意记录（含系统补录的 absent） */
    private boolean hasMorningRecord(Long userId, String clockDate) {
        LambdaQueryWrapper<SysAttendanceRecord> q = new LambdaQueryWrapper<>();
        q.eq(SysAttendanceRecord::getUserId, userId)
         .eq(SysAttendanceRecord::getClockDate, clockDate)
         .eq(SysAttendanceRecord::getClockType, ClockType.MORNING.getCode());
        return recordMapper.selectCount(q) > 0;
    }

    /**
     * 早上是否有真实打卡（排除09:30系统补录的 absent 记录）
     * 用于判断22:00是否需要补录晚上 early_leave
     */
    private boolean hasMorningActualPunch(Long userId, String clockDate) {
        LambdaQueryWrapper<SysAttendanceRecord> q = new LambdaQueryWrapper<>();
        q.eq(SysAttendanceRecord::getUserId, userId)
         .eq(SysAttendanceRecord::getClockDate, clockDate)
         .eq(SysAttendanceRecord::getClockType, ClockType.MORNING.getCode())
         .ne(SysAttendanceRecord::getStatus, AttendanceStatus.ABSENT.getCode());
        return recordMapper.selectCount(q) > 0;
    }

    /** 晚上是否有任意记录 */
    private boolean hasEveningRecord(Long userId, String clockDate) {
        LambdaQueryWrapper<SysAttendanceRecord> q = new LambdaQueryWrapper<>();
        q.eq(SysAttendanceRecord::getUserId, userId)
         .eq(SysAttendanceRecord::getClockDate, clockDate)
         .eq(SysAttendanceRecord::getClockType, ClockType.EVENING.getCode());
        return recordMapper.selectCount(q) > 0;
    }

    /** 查询指定用户指定日期指定类型的打卡记录（最多取1条） */
    private SysAttendanceRecord getRecord(Long userId, String clockDate, String clockType) {
        LambdaQueryWrapper<SysAttendanceRecord> q = new LambdaQueryWrapper<>();
        q.eq(SysAttendanceRecord::getUserId, userId)
         .eq(SysAttendanceRecord::getClockDate, clockDate)
         .eq(SysAttendanceRecord::getClockType, clockType)
         .last("LIMIT 1");
        return recordMapper.selectOne(q);
    }

    /** 补录早上未打卡记录（status=absent） */
    private void insertAbsentMorningRecord(SysUser user, LocalDate today, String clockDate) {
        SysAttendanceRecord record = new SysAttendanceRecord();
        record.setUserId(user.getId());
        record.setUserName(user.getName());
        record.setClockDate(clockDate);
        record.setClockType(ClockType.MORNING.getCode());
        record.setClockTime(Date.from(LocalDateTime.of(today, LocalTime.of(9, 0))
                .atZone(java.time.ZoneId.systemDefault()).toInstant()));
        record.setStatus(AttendanceStatus.ABSENT.getCode());
        record.setStatusDesc(AttendanceStatus.ABSENT.getDesc() + "（上班未打卡）");
        record.setRemark("系统自动记录：上班未打卡");
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        record.setIsDeleted(0);
        recordMapper.insert(record);
        log.info("用户 {} 今日上班未打卡，系统补录 absent 记录", user.getId());
    }

    /** 补录晚上未打卡记录（status=early_leave） */
    private void insertEarlyLeaveEveningRecord(SysUser user, LocalDate today, String clockDate) {
        SysAttendanceRecord record = new SysAttendanceRecord();
        record.setUserId(user.getId());
        record.setUserName(user.getName());
        record.setClockDate(clockDate);
        record.setClockType(ClockType.EVENING.getCode());
        record.setClockTime(Date.from(LocalDateTime.of(today, LocalTime.of(18, 0))
                .atZone(java.time.ZoneId.systemDefault()).toInstant()));
        record.setStatus(AttendanceStatus.EARLY_LEAVE.getCode());
        record.setStatusDesc(AttendanceStatus.EARLY_LEAVE.getDesc() + "（下班未打卡）");
        record.setRemark("系统自动记录：下班未打卡");
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        record.setIsDeleted(0);
        recordMapper.insert(record);
        log.info("用户 {} 今日下班未打卡，系统补录 early_leave 记录", user.getId());
    }

    /** 判断今天是否是周末 */
    private boolean isWeekend() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }
}
