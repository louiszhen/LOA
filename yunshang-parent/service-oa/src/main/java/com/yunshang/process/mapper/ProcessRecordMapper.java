package com.yunshang.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunshang.model.process.ProcessRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author nanfeng
 * @description 流程实例操作记录mapper类
 * @date 2023-03-19 13:17
 */
@Mapper
public interface ProcessRecordMapper extends BaseMapper<ProcessRecord> {
}
