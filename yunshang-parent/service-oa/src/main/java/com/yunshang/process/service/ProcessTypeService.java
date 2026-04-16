package com.yunshang.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunshang.model.process.ProcessType;

import java.util.List;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-17 20:44
 */
public interface ProcessTypeService extends IService<ProcessType> {

    /**
     * 查询审批分类
     */
    List<ProcessType> findProcessType();

    /**
     * 查询全部审批类型及已发布的模版
     */
    List<ProcessType> findPublishedProcessTypeWithTemplates();
}
