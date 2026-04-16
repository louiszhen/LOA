package com.yunshang.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yunshang.model.process.ProcessTemplate;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-17 21:11
 */
public interface ProcessTemplateService extends IService<ProcessTemplate> {

    /**
     * 分页查询审批模版，其中需要把审批类型对应的名称查询出来
     */
    IPage<ProcessTemplate> selectPageProcessTemplate(Page<ProcessTemplate> pageParam);

    /**
     * 发布审批模版
     */
    void publish(Long id);
}
