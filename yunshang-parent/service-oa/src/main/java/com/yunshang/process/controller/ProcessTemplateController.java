package com.yunshang.process.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yunshang.common.result.Result;
import com.yunshang.model.process.ProcessTemplate;
import com.yunshang.process.service.ProcessTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nanfeng
 * @description 审批模版Controller
 * @date 2023-03-17 21:13
 */
@Slf4j
@RestController
@RequestMapping(value = "/admin/process/processTemplate")
@SuppressWarnings({"rawtypes"})
public class ProcessTemplateController {

    @Resource
    private ProcessTemplateService processTemplateService;

    /**
     * 流程文件存储基础路径（从配置文件读取）
     */
    @Value("${process.storage-path}")
    private String processStoragePath;

    @PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page, @PathVariable Long limit) {
        Page<ProcessTemplate> pageParam = new Page<>(page, limit);
        IPage<ProcessTemplate> pageModel = processTemplateService.selectPageProcessTemplate(pageParam);
        return Result.ok(pageModel);
    }

    @PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessTemplate processTemplate = processTemplateService.getById(id);
        return Result.ok(processTemplate);
    }

    @PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @PostMapping("save")
    public Result save(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.save(processTemplate);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.updateById(processTemplate);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        processTemplateService.removeById(id);
        return Result.ok();
    }

    /**
     * 上传流程定义文件
     * 保存到配置的外部存储目录（支持JAR包部署）
     */
    @PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @PostMapping("/uploadProcessDefinition")
    public Result uploadProcessDefinition(MultipartFile file) throws IOException {
        // 安全处理文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return Result.fail("文件名不能为空");
        }

        // 提取纯文件名，去除路径（防止浏览器上传带路径的文件名）
        String filename = new File(originalFilename).getName();

        // 验证文件扩展名
        if (!filename.toLowerCase().endsWith(".zip")) {
            return Result.fail("只支持上传 ZIP 格式文件");
        }

        // 使用配置的外部存储路径
        File storageBaseDir = new File(processStoragePath);
        File processesDir = new File(storageBaseDir, "processes");
        log.info("流程文件存储目录: {}", processesDir.getAbsolutePath());

        // 创建目录
        if (!processesDir.exists()) {
            if (!processesDir.mkdirs()) {
                log.error("创建 processes 目录失败: {}", processesDir.getAbsolutePath());
                throw new IOException("无法创建上传目录: " + processesDir.getAbsolutePath());
            }
            log.info("创建 processes 目录成功");
        }

        // 处理文件名冲突
        File targetFile = new File(processesDir, filename);
        String finalFilename = filename;

        if (targetFile.exists()) {
            String nameWithoutExt = filename.substring(0, filename.lastIndexOf("."));
            String extension = filename.substring(filename.lastIndexOf("."));
            String timestamp = String.valueOf(System.currentTimeMillis());
            finalFilename = nameWithoutExt + "_" + timestamp + extension;
            targetFile = new File(processesDir, finalFilename);
            log.info("文件已存在，重命名为: {}", finalFilename);
        }

        // 保存文件
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(targetFile)) {
            fos.write(file.getBytes());
            log.info("文件保存成功: {}", targetFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("保存文件失败: {}", e.getMessage(), e);
            throw new IOException("保存文件失败: " + e.getMessage(), e);
        }

        // 返回结果（返回相对路径，统一使用 / 作为路径分隔符）
        Map<String, Object> map = new HashMap<>();
        map.put("processDefinitionPath", "processes/" + finalFilename);
        map.put("processDefinitionKey", finalFilename.substring(0, finalFilename.lastIndexOf(".")));
        return Result.ok(map);
    }

    /**
     * 发布审批模版
     */
    @PreAuthorize("hasAuthority('bnt.processTemplate.publish')")
    @GetMapping("/publish/{id}")
    public Result publish(@PathVariable Long id) {
        processTemplateService.publish(id);
        return Result.ok();
    }
}
