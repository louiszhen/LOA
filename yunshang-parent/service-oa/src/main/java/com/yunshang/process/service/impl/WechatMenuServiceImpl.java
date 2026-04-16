package com.yunshang.process.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunshang.model.wechat.Menu;
import com.yunshang.wechat.mapper.WechatMenuMapper;
import com.yunshang.process.service.WechatMenuService;
import com.yunshang.vo.wechat.MenuVo;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-19 20:24
 */
@Service
public class WechatMenuServiceImpl extends ServiceImpl<WechatMenuMapper, Menu> implements WechatMenuService {

    @Resource
    private WechatMenuMapper wechatMenuMapper;

    @Resource
    private WxMpService wxMpService;

    @Override
    public List<MenuVo> findMenuInfo() {
        // 1.查询所有菜单
        List<Menu> menuList = wechatMenuMapper.selectList(null);
        // 2.查询一级菜单-->parent_id=0
        List<Menu> firstLevelMenuList = menuList.stream().filter(menu -> menu.getParentId() == 0).toList();
        // 3.获取二级菜单并进行封装
        List<MenuVo> firstLevelMenuVoList = new ArrayList<>();
        for (Menu firstLevelMenu : firstLevelMenuList) {
            MenuVo firstLevelMenuVo = new MenuVo();
            BeanUtils.copyProperties(firstLevelMenu, firstLevelMenuVo);
            List<Menu> secondLevelMenuList = menuList.stream()
                    .filter(menu -> Objects.equals(menu.getParentId(), firstLevelMenu.getId()))
                    .toList();
            // menu --> menuVo
            List<MenuVo> secondLevelMenuVoList = new ArrayList<>();
            for (Menu secondLevelMenu : secondLevelMenuList) {
                MenuVo secondLevelMenuVo = new MenuVo();
                BeanUtils.copyProperties(secondLevelMenu, secondLevelMenuVo);
                secondLevelMenuVoList.add(secondLevelMenuVo);
            }
            firstLevelMenuVo.setChildren(secondLevelMenuVoList);
            firstLevelMenuVoList.add(firstLevelMenuVo);
        }
        return firstLevelMenuVoList;
    }

    @Override
    public void syncMenu() {
        // 查询菜单数据，封装成微信要求的菜单格式
        List<MenuVo> firstLevelMenuVoList = this.findMenuInfo();
        JSONArray buttonList = new JSONArray();
        for (MenuVo oneMenuVo : firstLevelMenuVoList) {
            JSONObject one = new JSONObject();
            one.put("name", oneMenuVo.getName());
            if (CollectionUtils.isEmpty(oneMenuVo.getChildren())) {
                one.put("type", oneMenuVo.getType());
                one.put("url", "http://kvjdgd.natappfree.cc/#" + oneMenuVo.getUrl());
            } else {
                JSONArray subButton = new JSONArray();
                for (MenuVo twoMenuVo : oneMenuVo.getChildren()) {
                    JSONObject view = new JSONObject();
                    view.put("type", twoMenuVo.getType());
                    if (twoMenuVo.getType().equals("view")) {
                        view.put("name", twoMenuVo.getName());
                        // H5页面地址
                        view.put("url", "http://kvjdgd.natappfree.cc#" + twoMenuVo.getUrl());
                    } else {
                        view.put("name", twoMenuVo.getName());
                        view.put("key", twoMenuVo.getMenuKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button", subButton);
            }
            buttonList.add(one);
        }
        JSONObject button = new JSONObject();
        button.put("button", buttonList);

        // 调用工具里面的方法实现菜单推送
        try {
            wxMpService.getMenuService().menuCreate(button.toJSONString());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeMenu() throws WxErrorException {
        wxMpService.getMenuService().menuDelete();
    }
}
