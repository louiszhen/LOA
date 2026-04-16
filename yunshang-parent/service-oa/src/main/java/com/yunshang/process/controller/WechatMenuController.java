package com.yunshang.process.controller;

import com.yunshang.common.result.Result;
import com.yunshang.model.wechat.Menu;
import com.yunshang.process.service.WechatMenuService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author nanfeng
 * @description 微信公众号菜单管理
 * @date 2023-03-19 20:26
 */
@RestController
@RequestMapping("/admin/wechat/menu")
@Slf4j
@SuppressWarnings("rawtypes")
public class WechatMenuController {

    @Resource
    private WechatMenuService wechatMenuService;

    @PreAuthorize("hasAuthority('bnt.menu.list')")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        Menu menu = wechatMenuService.getById(id);
        return Result.ok(menu);
    }

    @PreAuthorize("hasAuthority('bnt.menu.add')")
    @PostMapping("save")
    public Result save(@RequestBody Menu menu) {
        wechatMenuService.save(menu);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.menu.update')")
    @PutMapping("update")
    public Result updateById(@RequestBody Menu menu) {
        wechatMenuService.updateById(menu);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.menu.remove')")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        wechatMenuService.removeById(id);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.menu.list')")
    @GetMapping("findMenuInfo")
    public Result findMenuInfo() {
        return Result.ok(wechatMenuService.findMenuInfo());
    }

    /**
     * 同步微信菜单
     */
    @PreAuthorize("hasAuthority('bnt.menu.syncMenu')")
    @GetMapping("syncMenu")
    public Result createMenu() {
        wechatMenuService.syncMenu();
        return Result.ok();
    }

    /**
     * 删除微信端菜单
     */
    @PreAuthorize("hasAuthority('bnt.menu.removeMenu')")
    @DeleteMapping("removeMenu")
    public Result removeMenu() throws WxErrorException {
        wechatMenuService.removeMenu();
        return Result.ok();
    }}
