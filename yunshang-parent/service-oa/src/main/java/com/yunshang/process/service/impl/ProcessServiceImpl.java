package com.yunshang.process.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunshang.auth.service.SysUserService;
import com.yunshang.common.exception.YunshangException;
import com.yunshang.model.process.Process;
import com.yunshang.model.process.ProcessRecord;
import com.yunshang.model.process.ProcessTemplate;
import com.yunshang.model.process.ProcessType;
import com.yunshang.model.system.SysNotification;
import com.yunshang.model.system.SysUser;
import com.yunshang.process.mapper.ProcessMapper;
import com.yunshang.process.service.ProcessRecordService;
import com.yunshang.process.service.ProcessService;
import com.yunshang.process.service.ProcessTemplateService;
import com.yunshang.process.service.ProcessTypeService;
import com.yunshang.process.service.SseEmitterService;
import com.yunshang.process.service.SysNotificationService;
import com.yunshang.security.custom.LoginUserInfoHelper;
import com.yunshang.vo.process.ApprovalVo;
import com.yunshang.vo.process.FlowProgressVo;
import com.yunshang.vo.process.ProcessFormVo;
import com.yunshang.vo.process.ProcessQueryVo;
import com.yunshang.vo.process.ProcessVo;
//import com.yunshang.wechat.service.WechatMessageService;
import lombok.extern.slf4j.Slf4j;
//import me.chanjar.weixin.common.error.WxErrorException;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.exceptions.XMLException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author nanfeng
 * @description TODO
 * @date 2023-03-18 17:22
 */
@Slf4j
@Service
public class ProcessServiceImpl extends ServiceImpl<ProcessMapper, Process> implements ProcessService {

    /**
     * 流程文件存储基础路径（从配置文件读取）
     */
    @Value("${process.storage-path}")
    private String processStoragePath;

    @Resource
    private ProcessMapper processMapper;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private TaskService taskService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    @Lazy
    private ProcessTemplateService processTemplateService;

    @Resource
    @Lazy
    private ProcessTypeService processTypeService;

    @Resource
    private ProcessRecordService processRecordService;

    @Resource
    private HistoryService historyService;

    @Resource
    @Lazy
    private SysNotificationService sysNotificationService;

    @Resource
    @Lazy
    private SseEmitterService sseEmitterService;

//    @Resource
//    private WechatMessageService wechatMessageService;

    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        return processMapper.selectPage(pageParam, processQueryVo);
    }

    @Override
    public String deployByZip(String deployPath) {
        // 优先从外部存储目录读取（支持JAR包部署）
        File externalFile = new File(processStoragePath, deployPath);
        InputStream inputStream = null;
        
        if (externalFile.exists() && externalFile.isFile()) {
            // 从外部目录读取
            try {
                inputStream = new FileInputStream(externalFile);
                log.info("从外部目录加载流程文件: {}", externalFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                log.error("外部文件读取失败: {}", e.getMessage());
            }
        } else {
            // 回退到classpath读取（兼容旧数据）
            inputStream = this.getClass().getClassLoader().getResourceAsStream(deployPath);
            if (inputStream != null) {
                log.info("从 classpath 加载流程文件: {}", deployPath);
            }
        }
        
        if (inputStream == null) {
            log.error("部署流程失败：无法加载文件: {}\n外部路径: {}\nclasspath路径: {}", 
                    deployPath, externalFile.getAbsolutePath(), deployPath);
            throw new RuntimeException("流程定义文件不存在: " + deployPath);
        }
        try {
            // 解压 ZIP 并处理每个 BPMN 文件（移除 BOM）
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry;
            int processedCount = 0;
            String actualProcessDefinitionKey = null;
            Deployment deployment = null;

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                String entryNameLower = entryName.toLowerCase();

                // 跳过 macOS 的 AppleDouble 文件（以 ._ 开头）
                if (entryName.contains("/._") || entryName.startsWith("._")) {
                    log.info("跳过 macOS AppleDouble 文件: " + entryName);
                    zipInputStream.closeEntry();
                    continue;
                }

                // 只处理 BPMN 文件（包括 .bpmn 和 Activiti 标准格式 .bpmn20.xml）
                if (!entryNameLower.endsWith(".bpmn") && !entryNameLower.endsWith(".bpmn20.xml")) {
                    log.info("跳过非 BPMN 文件: " + entryName);
                    zipInputStream.closeEntry();
                    continue;
                }

                log.info("处理 BPMN 文件: " + entryName);

                // 读取整个文件内容
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                byte[] fileContent = baos.toByteArray();

                // 清理 BOM 和其他非标准字符
                byte[] cleanContent = cleanBpmnContent(fileContent);

                // 使用清理后的内容部署
                try (InputStream cleanedStream = new ByteArrayInputStream(cleanContent)) {
                    // 先创建 Deployment，后续获取流程定义
                    deployment = repositoryService.createDeployment()
                            .addInputStream(entryName, cleanedStream)
                            .name(entryName)
                            .deploy();
                    processedCount++;
                    log.info("成功部署 BPMN 文件: " + entryName);
                }
                zipInputStream.closeEntry();
            }

            if (processedCount == 0) {
                log.error("ZIP 文件中未找到任何有效的 BPMN 文件！");
                throw new RuntimeException("ZIP 文件中未找到任何有效的 BPMN 文件");
            }

            // 从最新部署的流程定义中获取实际的 key
            if (deployment != null) {
                // 获取该部署对应的流程定义（取最新版本）
                org.activiti.engine.repository.ProcessDefinition processDefinition = 
                    repositoryService.createProcessDefinitionQuery()
                        .deploymentId(deployment.getId())
                        .latestVersion()
                        .singleResult();
                if (processDefinition != null) {
                    actualProcessDefinitionKey = processDefinition.getKey();
                    log.info("部署成功！实际流程定义 key: " + actualProcessDefinitionKey + " (版本: " + processDefinition.getVersion() + ")");
                }
            }

            log.info("流程定义部署成功，共处理 " + processedCount + " 个 BPMN 文件");
            return actualProcessDefinitionKey;
        } catch (Exception e) {
            log.error("部署流程定义失败: " + e.getMessage(), e);
            throw new RuntimeException("部署流程定义失败: " + e.getMessage(), e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.warn("关闭输入流失败", e);
            }
        }
    }

    /**
     * 清理 BPMN 文件内容，确保以 <?xml 开头
     */
    private byte[] cleanBpmnContent(byte[] content) {
        if (content == null || content.length == 0) {
            log.error("BPMN 文件内容为空！");
            return content;
        }

        // 打印文件开头的十六进制，用于调试
        StringBuilder hexDebug = new StringBuilder("文件开头十六进制 (");
        hexDebug.append(Math.min(30, content.length)).append(" 字节): ");
        for (int i = 0; i < Math.min(30, content.length); i++) {
            hexDebug.append(String.format("%02X ", content[i] & 0xFF));
        }
        log.info(hexDebug.toString());

        // 方案1：检查标准 UTF-8 BOM (EF BB BF)
        if (content.length >= 3 &&
                (content[0] & 0xFF) == 0xEF &&
                (content[1] & 0xFF) == 0xBB &&
                (content[2] & 0xFF) == 0xBF) {
            log.info("检测到 UTF-8 BOM，跳过前3个字节");
            byte[] cleaned = new byte[content.length - 3];
            System.arraycopy(content, 3, cleaned, 0, cleaned.length);
            return cleaned;
        }

        // 方案2：检查 UTF-16 BOM 并转换
        if (content.length >= 2) {
            if ((content[0] & 0xFF) == 0xFE && (content[1] & 0xFF) == 0xFF) {
                log.info("检测到 UTF-16 BE BOM，转换编码");
                String str = new String(content, 2, content.length - 2, StandardCharsets.UTF_16BE);
                return str.getBytes(StandardCharsets.UTF_8);
            }
            if ((content[0] & 0xFF) == 0xFF && (content[1] & 0xFF) == 0xFE) {
                log.info("检测到 UTF-16 LE BOM，转换编码");
                String str = new String(content, 2, content.length - 2, StandardCharsets.UTF_16LE);
                return str.getBytes(StandardCharsets.UTF_8);
            }
        }

        // 方案3：尝试字符串方式（忽略解码异常）
        try {
            String contentStr = new String(content, 0, Math.min(500, content.length), StandardCharsets.UTF_8);
            int strXmlStart = contentStr.indexOf("<?xml");
            if (strXmlStart > 0) {
                log.info("字符串检测：清理 " + strXmlStart + " 个垃圾字符");
                String fullContent = new String(content, StandardCharsets.UTF_8);
                return fullContent.substring(strXmlStart).getBytes(StandardCharsets.UTF_8);
            }
            if (strXmlStart == 0) {
                log.info("文件开头正常，无需清理");
                return content;
            }
        } catch (Exception e) {
            log.warn("字符串解析失败: " + e.getMessage());
        }

        // 方案4：强制扫描（跳过所有非 ASCII 前导字符）
        int xmlStart = -1;
        for (int i = 0; i < content.length - 5; i++) {
            // 跳过所有非 ASCII 字符（前导垃圾）
            if ((content[i] & 0x80) != 0) continue;
            
            if (content[i] == '<' && 
                (i + 1 < content.length && (content[i+1] & 0xFF) == '?') &&
                (i + 2 < content.length && (content[i+2] & 0xFF) == 'x') &&
                (i + 3 < content.length && (content[i+3] & 0xFF) == 'm') &&
                (i + 4 < content.length && (content[i+4] & 0xFF) == 'l')) {
                xmlStart = i;
                break;
            }
        }

        if (xmlStart == 0) {
            log.info("文件开头正常");
            return content;
        } else if (xmlStart > 0) {
            log.info("字节扫描：跳过开头的 " + xmlStart + " 个垃圾字符");
            byte[] cleaned = new byte[content.length - xmlStart];
            System.arraycopy(content, xmlStart, cleaned, 0, cleaned.length);
            return cleaned;
        }

        // 方案5（最后尝试）：检查文件内容的前几个字符（可能是纯 ASCII）
        log.warn("所有 BOM 检测方案都失败，最后尝试...");
        
        // 打印文件内容的字符形式（如果是 ASCII）
        StringBuilder asciiDebug = new StringBuilder("文件前30个字符: ");
        for (int i = 0; i < Math.min(30, content.length); i++) {
            int c = content[i] & 0xFF;
            if (c >= 32 && c < 127) {
                asciiDebug.append((char) c);
            } else {
                asciiDebug.append(String.format("[%02X]", c));
            }
        }
        log.warn(asciiDebug.toString());

        // 返回原内容（让部署失败以便进一步诊断）
        return content;
    }

    /**
     * 移除 UTF-8 BOM 标记
     *
     * @param content 原始文件内容
     * @return 清理后的文件内容（如果没有 BOM，则返回原内容）
     */
    @Deprecated
    private byte[] removeBom(byte[] content) {
        return cleanBpmnContent(content);
    }

    @Override
    public void approve(ApprovalVo approvalVo) {
        // 获取任务id
        String taskId = approvalVo.getTaskId();

        // 如果前端没传 processId，通过 taskId 反查
        Long processId = approvalVo.getProcessId();
        if (processId == null) {
            // 先查运行中的任务
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            String processInstanceId = null;
            if (task != null) {
                processInstanceId = task.getProcessInstanceId();
            } else {
                // 任务可能已完成，查历史任务
                HistoricTaskInstance historicTask = historyService.createHistoricTaskInstanceQuery()
                        .taskId(taskId).singleResult();
                if (historicTask != null) {
                    processInstanceId = historicTask.getProcessInstanceId();
                }
            }
            if (processInstanceId != null) {
                LambdaQueryWrapper<Process> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Process::getProcessInstanceId, processInstanceId);
                Process proc = this.getOne(queryWrapper);
                if (proc != null) {
                    processId = proc.getId();
                    approvalVo.setProcessId(processId);
                }
            }
        }

        // 校验 processId 必须存在
        if (processId == null) {
            log.error("无法确定流程记录，taskId: {}", taskId);
            throw new YunshangException(208, "流程记录不存在");
        }

        // 查询流程信息（提前校验，避免任务完成后才发现流程不存在）
        Process process = this.getById(processId);
        if (process == null) {
            log.error("流程记录不存在，processId: {}", processId);
            throw new YunshangException(208, "流程记录不存在");
        }

        // 判断审批状态，1-->通过  2-->驳回
        if (approvalVo.getStatus() == 1) {
            // 检查任务是否存在，避免重复审批或任务已过期
            Task existingTask = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (existingTask != null) {
                // 在这里可以设置流程变量
                Map<String, Object> variables = new HashMap<>();
                taskService.complete(taskId, variables);
            } else {
                log.warn("任务不存在或已处理，taskId: {}", taskId);
            }
        } else {
            // 驳回，结束流程
            this.endTask(taskId);
        }

        // 记录审批相关过程信息 oa_process_record
        String description = approvalVo.getStatus() == 1 ? "已通过" : "驳回";
        processRecordService.record(processId, approvalVo.getStatus(), description);

        // 查询下一个审批人
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if (!CollectionUtils.isEmpty(taskList)) {
            List<String> assigneeList = new ArrayList<>();
            for (Task task : taskList) {
                SysUser sysUser = sysUserService.getByUsername(task.getAssignee());
                assigneeList.add(sysUser.getName());
                // 公众号推送消息
                // wechatMessageService.pushPendingMessage(process.getId(), sysUser.getId(), task.getId());
            }
            // 更新Process流程信息
            process.setDescription("等待" + String.join(",", assigneeList) + "审批");
            process.setStatus(1);
        } else {
            if (approvalVo.getStatus() == 1) {
                process.setDescription("审批完成（同意）");
                process.setStatus(2);
            } else {
                process.setDescription("审批完成（拒绝）");
                process.setStatus(-1);
            }
        }
        // 推送消息给申请人
        // wechatMessageService.pushProcessedMessage(process.getId(), process.getUserId(), approvalVo.getStatus());
        this.updateById(process);
    }

    /**
     * 驳回，直接终止整个流程实例
     */
    private void endTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            log.warn("endTask：任务不存在或已处理，taskId: {}", taskId);
            return;
        }
        runtimeService.deleteProcessInstance(task.getProcessInstanceId(), "审批驳回");
    }

    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {
        // 查询当前用户的已发起申请
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        return processMapper.selectPage(pageParam, processQueryVo);
    }

    @Override
    public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {
        // 根据当前人的ID查询
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .finished()
                .orderByTaskCreateTime()
                .desc();

        List<HistoricTaskInstance> list = query
                .listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), (int) pageParam.getSize());

        // 封装List<ProcessVo>
        List<ProcessVo> processList = new ArrayList<>();
        for (HistoricTaskInstance item : list) {
            String processInstanceId = item.getProcessInstanceId();
            LambdaQueryWrapper<Process> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Process::getProcessInstanceId, processInstanceId);
            Process process = this.getOne(queryWrapper);
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId("0");
            processList.add(processVo);
        }

        // 封装分页数据并返回
        IPage<ProcessVo> page = new Page<>(pageParam.getCurrent(), pageParam.getSize(), query.count());
        page.setRecords(processList);
        return page;
    }

    @Override
    public Map<String, Object> show(Long id) {
        // 1.获取Process对象
        Process process = this.getById(id);
        // 2.获取流程记录信息
        LambdaQueryWrapper<ProcessRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProcessRecord::getProcessId, id);
        List<ProcessRecord> processRecordList = processRecordService.list(queryWrapper);
        // 3.获取模版信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());

        // 4.判断当前用户是否可以审批，能够查看详情的用户不是都能审批，审批后也不能重复审批
        boolean isApprove = false;
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if (!CollectionUtils.isEmpty(taskList)) {
            for (Task task : taskList) {
                if (task.getAssignee().equals(LoginUserInfoHelper.getUsername())) {
                    isApprove = true;
                    break;
                }
            }
        }

        // 5.封装数据
        Map<String, Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", processRecordList);
        map.put("processTemplate", processTemplate);
        map.put("isApprove", isApprove);
        return map;
    }

    @Override
    public IPage<ProcessVo> findPending(Page<Process> pageParam) {
        // 封装查询条件
        TaskQuery taskQuery = taskService.createTaskQuery().taskAssignee(LoginUserInfoHelper.getUsername())
                .orderByTaskCreateTime().desc();
        // 分页查询
        List<Task> taskList = taskQuery
                .listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), (int) (pageParam.getSize()));

        // List<Task> --> List<ProcessVo>
        Long total = query().count();
        List<ProcessVo> processVoList = new ArrayList<>();
        for (Task task : taskList) {
            ProcessVo processVo = getProcessVoByTask(task);
            if (processVo != null) {
                processVoList.add(processVo);
            }
        }

        // 封装返回IPage对象
        IPage<ProcessVo> page = new Page<>(pageParam.getCurrent(), pageParam.getSize(), total);
        page.setRecords(processVoList);
        return page;
    }

    /**
     * 根据Task对象获取ProcessVo（与findPending接口返回字段一致）
     */
    private ProcessVo getProcessVoByTask(Task task) {
        if (task == null) {
            return null;
        }
        // 从task获取流程实例id
        String processInstanceId = task.getProcessInstanceId();
        // 根据流程实例id获取实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        // 兼容流程已结束的情况：从流程历史中获取businessKey
        if (processInstance == null) {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            if (historicProcessInstance == null) {
                log.warn("无法获取流程实例信息，跳过任务，taskId: {}", task.getId());
                return null;
            }
            String businessKey = historicProcessInstance.getBusinessKey();
            if (businessKey == null) {
                log.warn("流程实例businessKey为空，跳过任务，taskId: {}", task.getId());
                return null;
            }
            Process process = baseMapper.selectById(Long.valueOf(businessKey));
            if (process == null) {
                log.warn("无法找到对应的流程记录，跳过任务，taskId: {}, businessKey: {}", task.getId(), businessKey);
                return null;
            }
            return buildProcessVo(process, task.getId());
        }
        // 从流程实例中获取业务key，即processId
        String processId = processInstance.getBusinessKey();
        if (processId == null) {
            log.warn("流程实例businessKey为空，跳过任务，taskId: {}", task.getId());
            return null;
        }
        // 根据业务key获取Process对象
        Process process = baseMapper.selectById(Long.valueOf(processId));
        if (process == null) {
            log.warn("无法找到对应的流程记录，跳过任务，taskId: {}, businessKey: {}", task.getId(), processId);
            return null;
        }
        return buildProcessVo(process, task.getId());
    }

    @Override
    public ProcessVo buildProcessVo(Process process, String taskId) {
        if (process == null) {
            return null;
        }
        // Process --> ProcessVo
        ProcessVo processVo = new ProcessVo();
        BeanUtils.copyProperties(process, processVo);
        processVo.setTaskId(taskId);
        // 填充申请人姓名
        if (process.getUserId() != null) {
            SysUser user = sysUserService.getById(process.getUserId());
            if (user != null) {
                processVo.setName(user.getName());
            }
        }
        // 填充类型名称
        if (process.getProcessTypeId() != null) {
            ProcessType processType = processTypeService.getById(process.getProcessTypeId());
            if (processType != null) {
                processVo.setProcessTypeName(processType.getName());
            }
        }
        return processVo;
    }

    @Override
    public void startUp(ProcessFormVo processFormVo) {
        // 1.根据当前用户id获取用户信息
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        if (sysUser == null) {
            throw new YunshangException(209, "用户尚未登录！");
        }

        // 2.根据审批模版id查询模版信息
        ProcessTemplate processTemplate = processTemplateService.getById(processFormVo.getProcessTemplateId());
        if (processTemplate == null) {
            throw new YunshangException(201, "审批模板不存在！");
        }

        // 3.验证流程定义key
        String processDefinitionKey = processTemplate.getProcessDefinitionKey();
        if (!StringUtils.hasText(processDefinitionKey)) {
            log.error("启动流程失败：模板 {} (ID:{}) 的 process_definition_key 为空", 
                    processTemplate.getName(), processTemplate.getId());
            throw new YunshangException(201, "模板未配置流程定义key，请先上传并发布BPMN文件！");
        }

        // 验证流程是否已部署
        try {
            long count = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(processDefinitionKey)
                    .count();
            if (count == 0) {
                log.error("启动流程失败：流程定义 key '" + processDefinitionKey + "' 未部署，请先发布模板！");
                throw new YunshangException(201, "流程定义未部署，请先发布模板！");
            }
            log.info("找到流程定义 '" + processDefinitionKey + "'，已部署版本数：" + count);
        } catch (YunshangException e) {
            throw e;
        } catch (Exception e) {
            log.error("验证流程定义失败：" + e.getMessage());
            throw new YunshangException(201, "流程定义验证失败：" + e.getMessage());
        }

        // 4.保存提交审批信息到表oa_process
        Process process = new Process();
        BeanUtils.copyProperties(processFormVo, process);
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId()); // 用户id
        process.setFormValues(processFormVo.getFormValues()); // 表单信息
        process.setTitle(sysUser.getName() + "发起" + processTemplate.getName() + "申请"); // 流程实例标题
        process.setStatus(1); // 1 代表审批中
        processMapper.insert(process);

        // 5.启动流程实例 --> RuntimeService
        // 5.1 业务key --> processId
        String businessKey = String.valueOf(process.getId());
        // 5.2 流程参数 from表单json数据，转换map集合
        String formValues = processFormVo.getFormValues();
        JSONObject jsonObject = JSON.parseObject(formValues);
        JSONObject formData = jsonObject.getJSONObject("formData");
        // 将formData内容封装进map
        Map<String, Object> map = new HashMap<>(formData != null ? formData : new HashMap<>());
        // 注入 process 关键信息作为流程变量，供 TaskCreateListener 在事务内直接读取
        // 原因：TaskListener 在 Activiti 内部事务触发时，MyBatis 独立 Session 无法读取尚未提交的 oa_process 记录
        map.put("_processId", process.getId());
        map.put("_processTitle", process.getTitle());
        map.put("_processCode", process.getProcessCode());
        map.put("_processUserId", process.getUserId());
        // 5.3 启动流程实例
        log.info("启动流程实例：processDefinitionKey=" + processDefinitionKey + ", businessKey=" + businessKey);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, map);
        log.info("流程实例启动成功：instanceId=" + processInstance.getId());

        // 6.查询下一个审批人，并推送审批消息，审批人可能有多个
        List<Task> taskList = this.getCurrentTaskList(processInstance.getId());
        if (taskList != null && !taskList.isEmpty()) {
            List<String> assigneeList = taskList.stream().map(TaskInfo::getAssignee).toList();
            List<String> names = assigneeList.stream()
                    .map(a -> sysUserService.getByUsername(a))
                    .map(SysUser::getName).toList();

            // 6. 业务和流程关联，更新oa_process表，比如process_instance_id，description
            process.setProcessInstanceId(processInstance.getId());
            process.setDescription("等待" + String.join(",", names) + "审批");
        }
        processMapper.updateById(process);

        // 记录操作审批信息记录
        processRecordService.record(process.getId(), 1, "发起申请");
    }

    /**
     * 获取当前任务列表
     */
    private List<Task> getCurrentTaskList(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    }

    @Override
    public long getCurrentTaskCount(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).count();
    }

    @Override
    public FlowProgressVo getFlowProgress(Long processId) {
        // 1. 获取业务流程记录
        Process process = this.getById(processId);
        if (process == null) {
            throw new YunshangException(208, "流程记录不存在");
        }
        String processInstanceId = process.getProcessInstanceId();
        if (!StringUtils.hasText(processInstanceId)) {
            throw new YunshangException(208, "流程实例尚未启动");
        }

        // 2. 获取 processDefinitionId（优先从运行中实例取，已结束则从历史取）
        String processDefinitionId = null;
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (processInstance != null) {
            processDefinitionId = processInstance.getProcessDefinitionId();
        } else {
            HistoricProcessInstance historicProcessInstance = historyService
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            if (historicProcessInstance != null) {
                processDefinitionId = historicProcessInstance.getProcessDefinitionId();
            }
        }
        if (processDefinitionId == null) {
            throw new YunshangException(208, "无法获取流程定义信息");
        }

        // 3. 获取 BPMN XML（使用原始模型，不使用深拷贝副本）
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        BpmnXMLConverter converter = new BpmnXMLConverter();
        byte[] xmlBytes = converter.convertToXML(bpmnModel);
        String bpmnXml = new String(xmlBytes, StandardCharsets.UTF_8);

        // 4. 已完成节点 ID（绿色）
        List<String> completedActivityIds = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .finished()
                .list()
                .stream()
                .map(HistoricTaskInstance::getTaskDefinitionKey)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 5. 进行中节点 ID（红色）
        List<String> activeActivityIds = taskService
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .list()
                .stream()
                .map(Task::getTaskDefinitionKey)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 6. 封装返回
        FlowProgressVo vo = new FlowProgressVo();
        vo.setBpmnXml(bpmnXml);
        vo.setCompletedActivityIds(completedActivityIds);
        vo.setActiveActivityIds(activeActivityIds);
        vo.setProcessStatus(process.getStatus());
        return vo;
    }

    @Override
    public ProcessInstance getProcessInstanceById(String processInstanceId) {
        if (processInstanceId == null || processInstanceId.isEmpty()) {
            return null;
        }
        return runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
    }

    /**
     * 深拷贝 BpmnModel（通过 XML 序列化实现）
     * 用于创建流程定义的独立副本，避免修改污染缓存
     */
    private BpmnModel deepCopy(BpmnModel original) {
        try {
            // 使用 XML 序列化实现深拷贝
            BpmnXMLConverter converter = new BpmnXMLConverter();
            byte[] xmlBytes = converter.convertToXML(original);
            
            // 将 byte[] 转换为 XMLStreamReader
            ByteArrayInputStream bais = new ByteArrayInputStream(xmlBytes);
            javax.xml.stream.XMLInputFactory xmlInputFactory = javax.xml.stream.XMLInputFactory.newInstance();
            javax.xml.stream.XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(bais);
            
            BpmnModel copy = converter.convertToBpmnModel(xmlStreamReader);
            return copy;
        } catch (XMLException e) {
            throw new RuntimeException("BpmnModel 深拷贝失败: XML 转换异常", e);
        } catch (Exception e) {
            throw new RuntimeException("BpmnModel 深拷贝失败", e);
        }
    }
}