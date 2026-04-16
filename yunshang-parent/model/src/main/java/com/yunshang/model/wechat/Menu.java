package com.yunshang.model.wechat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yunshang.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-05 12:02
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("wechat_menu")
public class Menu extends BaseEntity {

    @TableField("parent_id")
    private Long parentId;

    private String name;

    private String type;

    /**
     * 网页 链接，用户点击菜单可打开链接
     */
    private String url;

    /**
     * 菜单KEY值，用于消息接口推送
     */
    @TableField("menu_key")
    private String menuKey;

    /**
     * 排序
     */
    private Integer sort;
}