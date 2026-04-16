package com.yunshang.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunshang.model.process.ProcessTemplate;
import com.yunshang.model.process.ProcessType;
import com.yunshang.process.mapper.ProcessTemplateMapper;
import com.yunshang.process.service.ProcessService;
import com.yunshang.process.service.ProcessTemplateService;
import com.yunshang.process.service.ProcessTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-17 21:12
 */
@Slf4j
@Service
public class ProcessTemplateServiceImpl extends ServiceImpl<ProcessTemplateMapper, ProcessTemplate> implements ProcessTemplateService {

    @Resource
    private ProcessTemplateMapper processTemplateMapper;

    @Resource
    @Lazy
    private ProcessTypeService processTypeService;

    @Resource
    private ProcessService processService;

    @Override
    public IPage<ProcessTemplate> selectPageProcessTemplate(Page<ProcessTemplate> pageParam) {
        // 分页查询ProcessTemplate
        LambdaQueryWrapper<ProcessTemplate> processTemplateQueryWrapper = new LambdaQueryWrapper<>();
        processTemplateQueryWrapper.orderByDesc(ProcessTemplate::getId);
        IPage<ProcessTemplate> page = processTemplateMapper.selectPage(pageParam, processTemplateQueryWrapper);
        List<ProcessTemplate> processTemplateList = page.getRecords();

        // 获取每个processTemplate的processTypeId，
        List<Long> processTypeIdList = processTemplateList.stream().map(ProcessTemplate::getProcessTypeId)
                .collect(Collectors.toList());

        // 根据流程类型id获取流程类型名称
        if (!CollectionUtils.isEmpty(processTypeIdList)) {
            LambdaQueryWrapper<ProcessType> processTypeQueryWrapper = new LambdaQueryWrapper<>();
            processTypeQueryWrapper.in(ProcessType::getId, processTypeIdList);
            Map<Long, String> processTypeMap = processTypeService.list(processTypeQueryWrapper)
                    .stream().collect(Collectors.toMap(ProcessType::getId, ProcessType::getName));
            for (ProcessTemplate processTemplate : processTemplateList) {
                String processTypeName = processTypeMap.get(processTemplate.getProcessTypeId());
                processTemplate.setProcessTypeName(processTypeName);
            }
        }
        return page;
    }

    @Transactional
    @Override
    public void publish(Long id) {
        ProcessTemplate processTemplate = this.getById(id);
        // 部署流程定义，获取实际的 processDefinitionKey
        String processDefinitionPath = processTemplate.getProcessDefinitionPath();
        if (StringUtils.hasText(processDefinitionPath)) {
            String actualProcessDefinitionKey = processService.deployByZip(processDefinitionPath);
            
            // 更新数据库中的 process_definition_key 为实际值（来自 BPMN 文件中 process 标签的 id 属性）
            if (StringUtils.hasText(actualProcessDefinitionKey)) {
                processTemplate.setProcessDefinitionKey(actualProcessDefinitionKey);
                log.info("更新模板 " + id + " 的 process_definition_key: " + actualProcessDefinitionKey);
            }
        }
        // 修改模版状态，1代表发布
        processTemplate.setStatus(1);
        processTemplateMapper.updateById(processTemplate);
    }
}
