package com.yunshang.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunshang.auth.mapper.SysDeptMapper;
import com.yunshang.auth.service.SysDeptService;
import com.yunshang.auth.service.SysDeptUserService;
import com.yunshang.model.system.SysDept;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * @author louiszhen
 * @description 部门Service实现
 * @date 2026-03-19
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Resource
    private SysDeptUserService sysDeptUserService;

    /**
     * 删除部门
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        // 删除部门用户关联记录
        sysDeptUserService.deleteByDeptId((Long) id);
        // 删除部门
        return super.removeById(id);
    }
}
