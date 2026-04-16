package com.yunshang.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunshang.model.system.SysDeptUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author louiszhen
 * @description 部门用户关联Mapper
 * @date 2026-03-19
 */
@Mapper
public interface SysDeptUserMapper extends BaseMapper<SysDeptUser> {
}
