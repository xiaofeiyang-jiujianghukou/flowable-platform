package com.flowable.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/process")
@Tag(name = "流程引擎", description = "流程定义、流程实例管理")
public class ProcessController {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;

    public ProcessController(RepositoryService repositoryService, RuntimeService runtimeService) {
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
    }

    // ──── 流程定义 ────

    @GetMapping("/definitions/{definitionId}/tasks")
    @Operation(summary = "获取流程定义的用户任务列表")
    public List<Map<String, Object>> getDefinitionTasks(@PathVariable String definitionId) {
        var bpmnModel = repositoryService.getBpmnModel(definitionId);
        List<Map<String, Object>> tasks = new ArrayList<>();
        if (bpmnModel != null) {
            for (var proc : bpmnModel.getProcesses()) {
                for (var el : proc.getFlowElements()) {
                    if (el instanceof org.flowable.bpmn.model.UserTask ut) {
                        Map<String, Object> t = new HashMap<>();
                        t.put("taskId", ut.getId());
                        t.put("taskName", ut.getName() != null ? ut.getName() : ut.getId());
                        t.put("assignee", ut.getAssignee() != null ? ut.getAssignee() : "");
                        t.put("candidateGroups", ut.getCandidateGroups() != null
                                ? new ArrayList<>(ut.getCandidateGroups()) : new ArrayList<>());
                        tasks.add(t);
                    }
                }
            }
        }
        return tasks;
    }

    @GetMapping("/definitions")
    @Operation(summary = "查询流程定义列表")
    public List<Map<String, Object>> listDefinitions() {
        return repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .list()
                .stream()
                .map(pd -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", pd.getId());
                    m.put("key", pd.getKey());
                    m.put("name", pd.getName());
                    m.put("version", pd.getVersion());
                    m.put("deploymentId", pd.getDeploymentId());
                    m.put("suspended", pd.isSuspended());
                    return m;
                })
                .toList();
    }

    @GetMapping("/definitions/key/{key}")
    @Operation(summary = "按Key获取最新流程定义")
    public Map<String, Object> getDefinitionByKey(@PathVariable String key) {
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(key).latestVersion().singleResult();
        if (pd == null) return Map.of();
        Map<String, Object> m = new HashMap<>();
        m.put("id", pd.getId()); m.put("key", pd.getKey()); m.put("name", pd.getName());
        m.put("version", pd.getVersion()); m.put("deploymentId", pd.getDeploymentId());
        return m;
    }

    @GetMapping("/definitions/{definitionId}")
    @Operation(summary = "获取流程定义详情")
    public Map<String, Object> getDefinition(@PathVariable String definitionId) {
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult();
        if (pd == null) return Map.of();
        Map<String, Object> m = new HashMap<>();
        m.put("id", pd.getId());
        m.put("key", pd.getKey());
        m.put("name", pd.getName());
        m.put("version", pd.getVersion());
        m.put("deploymentId", pd.getDeploymentId());
        m.put("description", pd.getDescription());
        return m;
    }

    // ──── 流程实例 ────

    @GetMapping("/instances")
    @Operation(summary = "查询流程实例列表")
    public List<Map<String, Object>> listInstances() {
        return runtimeService.createProcessInstanceQuery()
                .list()
                .stream()
                .map(pi -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", pi.getId());
                    m.put("processDefinitionId", pi.getProcessDefinitionId());
                    m.put("processDefinitionKey", pi.getProcessDefinitionKey());
                    m.put("processDefinitionName", pi.getProcessDefinitionName());
                    m.put("businessKey", pi.getBusinessKey());
                    m.put("startUserId", pi.getStartUserId());
                    m.put("startTime", pi.getStartTime());
                    m.put("suspended", pi.isSuspended());
                    return m;
                })
                .toList();
    }

    @PostMapping("/instances")
    @Operation(summary = "启动流程实例")
    public Map<String, Object> startInstance(
            @Parameter(description = "流程定义Key") @RequestParam String processDefinitionKey,
            @Parameter(description = "启动变量") @RequestBody(required = false) Map<String, Object> variables) {
        ProcessInstance pi = (variables != null && !variables.isEmpty())
                ? runtimeService.startProcessInstanceByKey(processDefinitionKey, variables)
                : runtimeService.startProcessInstanceByKey(processDefinitionKey);
        Map<String, Object> m = new HashMap<>();
        m.put("id", pi.getId());
        m.put("processDefinitionId", pi.getProcessDefinitionId());
        m.put("businessKey", pi.getBusinessKey());
        return m;
    }

    @GetMapping("/instances/{instanceId}")
    @Operation(summary = "获取流程实例详情")
    public Map<String, Object> getInstance(@PathVariable String instanceId) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();
        if (pi == null) return Map.of();
        Map<String, Object> m = new HashMap<>();
        m.put("id", pi.getId());
        m.put("processDefinitionId", pi.getProcessDefinitionId());
        m.put("processDefinitionKey", pi.getProcessDefinitionKey());
        m.put("businessKey", pi.getBusinessKey());
        m.put("startUserId", pi.getStartUserId());
        m.put("startTime", pi.getStartTime());
        m.put("activityId", pi.getActivityId());
        return m;
    }

    @DeleteMapping("/instances/{instanceId}")
    @Operation(summary = "删除流程实例")
    public Map<String, String> deleteInstance(
            @PathVariable String instanceId,
            @Parameter(description = "删除原因") @RequestParam(defaultValue = "手动删除") String reason) {
        runtimeService.deleteProcessInstance(instanceId, reason);
        return Map.of("result", "deleted");
    }
}
