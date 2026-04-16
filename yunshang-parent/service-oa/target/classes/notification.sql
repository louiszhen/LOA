-- =============================================
-- 通知表 (oa_notification)
-- =============================================

-- 创建通知表
CREATE TABLE IF NOT EXISTS `oa_notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    
    -- 通知基本信息
    `type` INT NOT NULL DEFAULT 1 COMMENT '通知类型: 1-待审核任务通知, 2-流程完成通知, 3-流程驳回通知',
    `title` VARCHAR(255) NOT NULL COMMENT '通知标题',
    `content` TEXT COMMENT '通知内容',
    
    -- 关联信息
    `process_id` BIGINT COMMENT '关联的流程ID',
    `task_id` VARCHAR(64) COMMENT '关联的流程任务ID',
    `user_id` BIGINT NOT NULL COMMENT '通知接收人用户ID',
    
    -- 状态
    `status` INT NOT NULL DEFAULT 0 COMMENT '通知状态: 0-未读, 1-已读, 2-已处理(已审核)',
    
    -- 扩展数据
    `extra_data` TEXT COMMENT '扩展字段JSON，存储额外信息（如流程类型、申请人等）',
    
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_user_status` (`user_id`, `status`),
    INDEX `idx_process_id` (`process_id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- =============================================
-- 说明：
-- 1. type: 通知类型
--    - 1: 待审核任务通知（新任务创建时发送给审批人）
--    - 2: 流程完成通知（流程审批通过后发送给申请人）
--    - 3: 流程驳回通知（流程被驳回后发送给申请人）
--
-- 2. status: 通知状态
--    - 0: 未读（用户尚未查看）
--    - 1: 已读（用户已查看但尚未处理）
--    - 2: 已处理（用户已处理该通知，如完成审批）
--
-- 3. extra_data: 扩展数据JSON示例
--    {
--      "processCode": "1234567890",
--      "processTitle": "请假申请",
--      "taskName": "部门经理审批",
--      "applicantName": "张三",
--      "applicantId": 1,
--      "description": "等待李四审批"
--    }
-- =============================================

