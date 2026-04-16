package com.yunshang.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunshang.model.system.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-10 13:58
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    List<SysMenu> findListByUserId(Long userId);
}
