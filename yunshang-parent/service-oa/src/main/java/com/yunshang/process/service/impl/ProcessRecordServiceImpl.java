package com.yunshang.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunshang.auth.service.SysUserService;
import com.yunshang.model.process.ProcessRecord;
import com.yunshang.model.system.SysUser;
import com.yunshang.process.mapper.ProcessRecordMapper;
import com.yunshang.process.service.ProcessRecordService;
import com.yunshang.security.custom.LoginUserInfoHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-19 13:19
 */
@Service
public class ProcessRecordServiceImpl extends ServiceImpl<ProcessRecordMapper, ProcessRecord> implements ProcessRecordService {

    @Resource
    private ProcessRecordMapper processRecordMapper;

    @Resource
    private SysUserService sysUserService;

    @Override
    public void record(Long processId, Integer status, String description) {
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        ProcessRecord processRecord = new ProcessRecord();
        // 流程id
        processRecord.setProcessId(processId);
        // 审批状态
        processRecord.setStatus(status);
        // 描述信息
        processRecord.setDescription(description);
        // 当前操作用户id
        processRecord.setOperateUserId(sysUser.getId());
        // 当前操作用户名称
        processRecord.setOperateUser(sysUser.getName());
        processRecordMapper.insert(processRecord);
    }

}