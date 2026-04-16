package com.yunshang.model.process;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yunshang.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-05 11:34
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("oa_process_record")
public class ProcessRecord extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("process_id")
    private Long processId;

    @TableField("description")
    private String description;

    @TableField("status")
    private Integer status;

    @TableField("operate_user_id")
    private Long operateUserId;

    @TableField("operate_user")
    private String operateUser;
}
