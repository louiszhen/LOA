package com.yunshang.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunshang.model.system.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-08 20:44
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 批量插入用户角色关联
     * @param list 用户角色列表
     * @return 插入记录数
     */
    int insertBatch(@Param("list") List<SysUserRole> list);
}
