package com.yunshang.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunshang.auth.mapper.SysRoleMapper;
import com.yunshang.auth.mapper.SysUserRoleMapper;
import com.yunshang.auth.service.SysUserRoleService;
import com.yunshang.model.system.SysUserRole;
import org.springframework.stereotype.Service;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-08 20:45
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {
}
