package com.flowable.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flowable.usercenter.entity.ProcessConfig;
import com.flowable.usercenter.feign.FlowableClient;
import com.flowable.usercenter.mapper.ProcessConfigMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/approval")
@Tag(name = "审批管理", description = "发起审批、待审批、已审批")
public class ApprovalController {

    private final ProcessConfigMapper configMapper;
    private final FlowableClient flowableClient;

    public ApprovalController(ProcessConfigMapper configMapper, FlowableClient flowableClient) {
        this.configMapper = configMapper;
        this.flowableClient = flowableClient;
    }

    @GetMapping("/published-configs")
    @Operation(summary = "获取已发布流程（用于发起审批）")
    public List<ProcessConfig> publishedConfigs() {
        return configMapper.selectList(
            new LambdaQueryWrapper<ProcessConfig>().eq(ProcessConfig::getPublished, true));
    }

    @PostMapping("/start")
    @Operation(summary = "发起审批申请")
    public Map<String, Object> start(
            @Parameter(description = "流程配置ID") @RequestParam Long configId,
            @Parameter(description = "发起人") @RequestParam(defaultValue = "当前用户") String initiator,
            @Parameter(description = "表单数据") @RequestBody Map<String, Object> formData) {
        ProcessConfig config = configMapper.selectById(configId);
        Map<String, Object> vars = new HashMap<>(formData);
        vars.put("initiator", initiator);
        return flowableClient.startProcess(config.getProcessDefinitionKey(), vars);
    }

    @GetMapping("/pending")
    @Operation(summary = "待审批任务列表")
    public Object pending(@Parameter(description = "当前用户") @RequestParam(defaultValue = "当前用户") String user) {
        return flowableClient.listTasks(user);
    }

    @PostMapping("/{taskId}/approve")
    @Operation(summary = "审批通过")
    public Map<String, String> approve(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            @Parameter(description = "审批意见") @RequestBody(required = false) Map<String, Object> comment) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("approved", true);
        if (comment != null && comment.containsKey("comment")) vars.put("approvalComment", comment.get("comment"));
        flowableClient.completeTask(taskId, vars);
        return Map.of("result", "approved");
    }

    @PostMapping("/{taskId}/reject")
    @Operation(summary = "审批驳回")
    public Map<String, String> reject(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            @Parameter(description = "驳回意见") @RequestBody(required = false) Map<String, Object> comment) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("approved", false);
        if (comment != null && comment.containsKey("comment")) vars.put("approvalComment", comment.get("comment"));
        flowableClient.completeTask(taskId, vars);
        return Map.of("result", "rejected");
    }

    @GetMapping("/history")
    @Operation(summary = "已审批历史记录")
    public Object history() { return flowableClient.historyTasks(); }
}
