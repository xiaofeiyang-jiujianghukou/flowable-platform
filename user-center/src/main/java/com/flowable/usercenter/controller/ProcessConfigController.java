package com.flowable.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowable.usercenter.entity.ProcessConfig;
import com.flowable.usercenter.feign.FlowableClient;
import com.flowable.usercenter.mapper.ProcessConfigMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/process-configs")
@Tag(name = "流程配置", description = "发布流程、配置审批人员")
public class ProcessConfigController {

    private final ProcessConfigMapper configMapper;
    private final FlowableClient flowableClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProcessConfigController(ProcessConfigMapper configMapper, FlowableClient flowableClient) {
        this.configMapper = configMapper;
        this.flowableClient = flowableClient;
    }

    @GetMapping("/definitions")
    @Operation(summary = "获取已部署的流程定义列表")
    public Object listDefinitions() { return flowableClient.listDefinitions(); }

    @GetMapping
    @Operation(summary = "查询流程配置列表")
    public List<ProcessConfig> list() {
        return configMapper.selectList(new LambdaQueryWrapper<ProcessConfig>().orderByDesc(ProcessConfig::getUpdatedAt));
    }

    @PostMapping
    @Operation(summary = "创建流程配置")
    public Map<String, String> create(
            @Parameter(description = "流程定义Key") @RequestParam String processDefinitionKey,
            @Parameter(description = "配置名称") @RequestParam String name) {
        var pd = flowableClient.getDefinitionByKey(processDefinitionKey);
        String defId = pd != null ? (String) pd.get("id") : "";
        String processName = pd != null ? (String) pd.get("name") : processDefinitionKey;

        // 提取用户任务定义
        List<Map<String, Object>> tasks = defId.isEmpty() ? List.of()
            : flowableClient.getDefinitionTasks(defId);

        ProcessConfig config = new ProcessConfig();
        config.setName(name);
        config.setProcessDefinitionKey(processDefinitionKey);
        config.setProcessDefinitionId(defId);
        config.setProcessName(processName);
        config.setTaskAssignees(tasks.isEmpty() ? "[]" : writeJson(tasks));
        config.setPublished(false);
        configMapper.insert(config);
        return Map.of("result", "created", "id", config.getId().toString());
    }

    @PutMapping("/{id}/assignees")
    @Operation(summary = "保存任务处理人配置")
    public Map<String, String> saveAssignees(@PathVariable Long id, @RequestBody String assignees) {
        ProcessConfig config = configMapper.selectById(id);
        config.setTaskAssignees(assignees);
        configMapper.updateById(config);
        return Map.of("result", "saved");
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "发布流程配置")
    public Map<String, String> publish(@Parameter(description = "配置ID") @PathVariable Long id) {
        ProcessConfig config = configMapper.selectById(id);
        config.setPublished(true);
        configMapper.updateById(config);
        return Map.of("result", "published");
    }

    @PostMapping("/{id}/unpublish")
    @Operation(summary = "取消发布")
    public Map<String, String> unpublish(@Parameter(description = "配置ID") @PathVariable Long id) {
        ProcessConfig config = configMapper.selectById(id);
        config.setPublished(false);
        configMapper.updateById(config);
        return Map.of("result", "unpublished");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除配置")
    public Map<String, String> delete(@Parameter(description = "配置ID") @PathVariable Long id) {
        configMapper.deleteById(id);
        return Map.of("result", "deleted");
    }

    private String writeJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); }
        catch (Exception e) { return "[]"; }
    }
}
