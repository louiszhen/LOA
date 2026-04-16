-- ============================================
-- 考勤打卡功能数据库DDL脚本
-- ============================================

-- ----------------------------
-- 1. 打卡明细表 sys_attendance_record
-- ----------------------------
DROP TABLE IF EXISTS `sys_attendance_record`;
CREATE TABLE `sys_attendance_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `user_name` VARCHAR(50) DEFAULT NULL COMMENT '用户姓名',
  `clock_date` VARCHAR(10) NOT NULL COMMENT '打卡日期 (格式: yyyy-MM-dd)',
  `clock_type` VARCHAR(20) NOT NULL COMMENT '打卡类型: morning-上班, evening-下班',
  `clock_time` DATETIME DEFAULT NULL COMMENT '打卡时间',
  `status` VARCHAR(20) NOT NULL COMMENT '打卡状态: normal-正常, late-迟到, early_leave-早退, absent-未打卡',
  `status_desc` VARCHAR(100) DEFAULT NULL COMMENT '打卡状态描述',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_date_type` (`user_id`, `clock_date`, `clock_type`),
  KEY `idx_clock_date` (`clock_date`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡明细表';

-- ----------------------------
-- 2. 打卡统计表 sys_attendance_statistics
-- ----------------------------
DROP TABLE IF EXISTS `sys_attendance_statistics`;
CREATE TABLE `sys_attendance_statistics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `user_name` VARCHAR(50) DEFAULT NULL COMMENT '用户姓名',
  `stat_month` VARCHAR(6) NOT NULL COMMENT '统计月份 (格式: yyyyMM)',
  `should_clock_days` INT DEFAULT 0 COMMENT '应打卡天数',
  `actual_clock_days` INT DEFAULT 0 COMMENT '实际打卡天数',
  `normal_clock_days` INT DEFAULT 0 COMMENT '正常打卡天数',
  `late_days` INT DEFAULT 0 COMMENT '迟到天数',
  `early_leave_days` INT DEFAULT 0 COMMENT '早退天数',
  `absent_days` INT DEFAULT 0 COMMENT '未打卡天数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_month` (`user_id`, `stat_month`),
  KEY `idx_stat_month` (`stat_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡统计表';

-- ----------------------------
-- 3. Redis Bitmap
-- ----------------------------
-- Key: attendance:bitmap:yyyyMMdd_morning
-- Key: attendance:bitmap:yyyyMMdd_evening
-- Example: attendance:bitmap:20260320_morning
-- userId as offset, 0=not clocked, 1=clocked, TTL=30 days

-- ----------------------------
-- 4. Redisson Queue
-- ----------------------------
-- Name: attendance:queue  Message: AttendanceMessageVo
-- Consumer: local buffer (LinkedBlockingQueue) + dual-trigger flush
--   - count trigger: flush on 50 messages
--   - time trigger:  flush every 5 seconds
-- Handler: batch INSERT records only (statistics settled by scheduled task)

-- ----------------------------
-- 5. Scheduled Tasks
-- ----------------------------
-- 09:30 daily: supplement morning absence record (status=absent, no statistics update)
-- 22:00 daily: supplement evening absence + six-dimension settlement + delete Redis cache

-- ============================================
-- TEST DATA: 2026-01-13 to 2026-01-17 (5 days, all normal)
-- userId=1 (admin), userId=3 (zhangsan)
-- Six-dimension Rule 3: morning=normal + evening=normal -> normal_clock_days+1
-- ============================================

-- ----------------------------
-- sys_attendance_record
-- morning punch before 09:30 = normal
-- evening punch after 17:30 = normal
-- ----------------------------
DELETE FROM `sys_attendance_record` WHERE user_id IN (1, 3) AND clock_date BETWEEN '2026-01-13' AND '2026-01-17';
INSERT INTO `sys_attendance_record`
  (`user_id`, `user_name`, `clock_date`, `clock_type`, `clock_time`,
   `status`, `status_desc`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES
-- admin (userId=1)
(1, 'admin', '2026-01-13', 'morning', '2026-01-13 08:45:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-13 08:45:00', '2026-01-13 08:45:00', 0),
(1, 'admin', '2026-01-13', 'evening', '2026-01-13 18:05:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-13 18:05:00', '2026-01-13 18:05:00', 0),
(1, 'admin', '2026-01-14', 'morning', '2026-01-14 08:52:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-14 08:52:00', '2026-01-14 08:52:00', 0),
(1, 'admin', '2026-01-14', 'evening', '2026-01-14 18:20:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-14 18:20:00', '2026-01-14 18:20:00', 0),
(1, 'admin', '2026-01-15', 'morning', '2026-01-15 09:10:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-15 09:10:00', '2026-01-15 09:10:00', 0),
(1, 'admin', '2026-01-15', 'evening', '2026-01-15 17:45:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-15 17:45:00', '2026-01-15 17:45:00', 0),
(1, 'admin', '2026-01-16', 'morning', '2026-01-16 08:58:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-16 08:58:00', '2026-01-16 08:58:00', 0),
(1, 'admin', '2026-01-16', 'evening', '2026-01-16 18:35:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-16 18:35:00', '2026-01-16 18:35:00', 0),
(1, 'admin', '2026-01-17', 'morning', '2026-01-17 09:05:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-17 09:05:00', '2026-01-17 09:05:00', 0),
(1, 'admin', '2026-01-17', 'evening', '2026-01-17 17:55:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-17 17:55:00', '2026-01-17 17:55:00', 0),
-- zhangsan (userId=3)
(3, '张三', '2026-01-13', 'morning', '2026-01-13 08:50:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-13 08:50:00', '2026-01-13 08:50:00', 0),
(3, '张三', '2026-01-13', 'evening', '2026-01-13 18:10:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-13 18:10:00', '2026-01-13 18:10:00', 0),
(3, '张三', '2026-01-14', 'morning', '2026-01-14 09:05:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-14 09:05:00', '2026-01-14 09:05:00', 0),
(3, '张三', '2026-01-14', 'evening', '2026-01-14 18:15:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-14 18:15:00', '2026-01-14 18:15:00', 0),
(3, '张三', '2026-01-15', 'morning', '2026-01-15 08:45:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-15 08:45:00', '2026-01-15 08:45:00', 0),
(3, '张三', '2026-01-15', 'evening', '2026-01-15 17:50:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-15 17:50:00', '2026-01-15 17:50:00', 0),
(3, '张三', '2026-01-16', 'morning', '2026-01-16 09:00:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-16 09:00:00', '2026-01-16 09:00:00', 0),
(3, '张三', '2026-01-16', 'evening', '2026-01-16 18:00:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-16 18:00:00', '2026-01-16 18:00:00', 0),
(3, '张三', '2026-01-17', 'morning', '2026-01-17 08:55:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-17 08:55:00', '2026-01-17 08:55:00', 0),
(3, '张三', '2026-01-17', 'evening', '2026-01-17 17:40:00', 'normal', '正常', '通过Redis Bitmap记录打卡', '2026-01-17 17:40:00', '2026-01-17 17:40:00', 0);

-- ----------------------------
-- sys_attendance_statistics
-- Jan 2026 working days: 22
--   (1,2,5,6,7,8,9,12,13,14,15,16,19,20,21,22,23,26,27,28,29,30)
-- 5 days all normal (six-dimension rule 3):
--   actual_clock_days=5, normal_clock_days=5
--   late_days=0, early_leave_days=0, absent_days=0
-- create_time = 2026-01-17 22:00:00 (checkEveningAndSettle settlement time)
-- ----------------------------
DELETE FROM `sys_attendance_statistics` WHERE user_id IN (1, 3) AND stat_month = '202601';
INSERT INTO `sys_attendance_statistics`
  (`user_id`, `user_name`, `stat_month`,
   `should_clock_days`, `actual_clock_days`, `normal_clock_days`,
   `late_days`, `early_leave_days`, `absent_days`,
   `create_time`, `update_time`, `is_deleted`)
VALUES
(1, 'admin', '202601', 22, 5, 5, 0, 0, 0, '2026-01-17 22:00:00', '2026-01-17 22:00:00', 0),
(3, '张三',  '202601', 22, 5, 5, 0, 0, 0, '2026-01-17 22:00:00', '2026-01-17 22:00:00', 0);

