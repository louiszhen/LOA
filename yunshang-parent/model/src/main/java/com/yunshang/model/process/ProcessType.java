package com.yunshang.model.process;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yunshang.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-05 11:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("oa_process_type")
public class ProcessType extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField(exist = false)
    private List<ProcessTemplate> processTemplateList;
}
