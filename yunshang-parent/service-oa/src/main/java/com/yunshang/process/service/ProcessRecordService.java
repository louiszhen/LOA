package com.yunshang.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunshang.model.process.ProcessRecord;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-19 13:18
 */
public interface ProcessRecordService extends IService<ProcessRecord> {

    void record(Long processId, Integer status, String description);
}
