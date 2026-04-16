package com.yunshang.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunshang.model.process.ProcessTemplate;
import com.yunshang.model.process.ProcessType;
import com.yunshang.process.mapper.ProcessTypeMapper;
import com.yunshang.process.service.ProcessTemplateService;
import com.yunshang.process.service.ProcessTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-17 20:45
 */
@Service
public class ProcessTypeServiceImpl extends ServiceImpl<ProcessTypeMapper, ProcessType> implements ProcessTypeService {

    @Resource
    private ProcessTemplateService processTemplateService;

    @Override
    public List<ProcessType> findProcessType() {
        // 查询所有审批分类，返回list集合
        List<ProcessType> processTypeList = baseMapper.selectList(null);

        // 遍历返回所有审批分类list集合
        for (ProcessType processType : processTypeList) {
            // 得到每个审批分类，根据审批分类id查询对应审批模板
            // 审批分类id
            Long typeId = processType.getId();
            // 根据审批分类id查询对应审批模板
            LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId, typeId);
            List<ProcessTemplate> processTemplateList = processTemplateService.list(wrapper);

            // 根据审批分类id查询对应审批模板数据（List）封装到每个审批分类对象里面
            processType.setProcessTemplateList(processTemplateList);
        }
        return processTypeList;
    }

    @Override
    public List<ProcessType> findPublishedProcessTypeWithTemplates() {
        // 查询所有审批分类，返回 list 集合
        List<ProcessType> processTypeList = baseMapper.selectList(null);

        // 遍历返回所有审批分类 list 集合
        for (ProcessType processType : processTypeList) {
            // 得到每个审批分类，根据审批分类 id 查询对应已发布的审批模板
            // 审批分类 id
            Long typeId = processType.getId();
            // 根据审批分类 id 查询对应已发布的审批模板（只查询 status=1 的已发布模板）
            LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId, typeId);
            wrapper.eq(ProcessTemplate::getStatus, 1); // 只查询已发布的模板
            List<ProcessTemplate> processTemplateList = processTemplateService.list(wrapper);

            // 根据审批分类 id 查询对应审批模板数据（List）封装到每个审批分类对象里面
            processType.setProcessTemplateList(processTemplateList);
        }
        return processTypeList;
    }
}
