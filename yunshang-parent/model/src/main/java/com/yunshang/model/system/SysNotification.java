package com.yunshang.model.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yunshang.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @description 通知表
 * @author 
 * @date 2026-03-24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("oa_notification")
public class SysNotification extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 通知类型: 1-待审核任务通知, 2-流程完成通知, 3-流程驳回通知
     */
    @TableField("type")
    private Integer type;

    /**
     * 通知标题
     */
    @TableField("title")
    private String title;

    /**
     * 通知内容
     */
    @TableField("content")
    private String content;

    /**
     * 关联的流程ID
     */
    @TableField("process_id")
    private Long processId;

    /**
     * 关联的流程任务ID
     */
    @TableField("task_id")
    private String taskId;

    /**
     * 通知接收人用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 通知状态: 0-未读, 1-已读, 2-已处理(已审核)
     */
    @TableField("status")
    private Integer status;

    /**
     * 扩展字段JSON，存储额外信息（如流程类型、申请人等）
     */
    @TableField("extra_data")
    private String extraData;
}
