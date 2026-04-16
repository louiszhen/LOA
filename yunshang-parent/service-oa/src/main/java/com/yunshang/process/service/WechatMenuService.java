package com.yunshang.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunshang.model.wechat.Menu;
import com.yunshang.vo.wechat.MenuVo;
import me.chanjar.weixin.common.error.WxErrorException;

import java.util.List;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-19 20:24
 */
public interface WechatMenuService extends IService<Menu> {

    /**
     * 获取微信端菜单信息，返回的都是一级菜单，里面包含了二级菜单
     */
    List<MenuVo> findMenuInfo();

    /**
     * 同步微信菜单
     */
    void syncMenu();

    /**
     * 删除推送菜单
     */
    void removeMenu() throws WxErrorException;
}
