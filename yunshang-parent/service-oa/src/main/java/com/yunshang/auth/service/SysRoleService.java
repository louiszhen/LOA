package com.yunshang.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunshang.model.system.SysRole;
import com.yunshang.vo.system.AssignRoleVo;

import java.util.Map;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-06 15:06
 */
public interface SysRoleService extends IService<SysRole> {
    /**
     * 获取用户角色数据
     */
    Map<String, Object> findRoleByUserId(Long userId);

    /**
     * 为用户分配角色
     */
    void doAssign(AssignRoleVo assignRoleVo);
}
