package com.flowable.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.task.api.Task;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cmmn")
@Tag(name = "CMMN 案例引擎", description = "案例管理与任务")
public class CmmnController {

    private final CmmnRepositoryService cmmnRepositoryService;
    private final CmmnRuntimeService cmmnRuntimeService;
    private final CmmnTaskService cmmnTaskService;

    public CmmnController(CmmnRepositoryService cmmnRepositoryService,
                          CmmnRuntimeService cmmnRuntimeService,
                          CmmnTaskService cmmnTaskService) {
        this.cmmnRepositoryService = cmmnRepositoryService;
        this.cmmnRuntimeService = cmmnRuntimeService;
        this.cmmnTaskService = cmmnTaskService;
    }

    @GetMapping("/case-definitions")
    @Operation(summary = "查询案例定义列表")
    public List<Map<String, Object>> listCaseDefinitions() {
        return cmmnRepositoryService.createCaseDefinitionQuery()
                .latestVersion()
                .list()
                .stream()
                .map(cd -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", cd.getId());
                    m.put("key", cd.getKey());
                    m.put("name", cd.getName());
                    m.put("version", cd.getVersion());
                    m.put("deploymentId", cd.getDeploymentId());
                    return m;
                })
                .toList();
    }

    @GetMapping("/case-instances")
    @Operation(summary = "查询案例实例列表")
    public List<Map<String, Object>> listCaseInstances() {
        return cmmnRuntimeService.createCaseInstanceQuery()
                .list()
                .stream()
                .map(ci -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", ci.getId());
                    m.put("caseDefinitionId", ci.getCaseDefinitionId());
                    m.put("caseDefinitionKey", ci.getCaseDefinitionKey());
                    m.put("caseDefinitionName", ci.getCaseDefinitionName());
                    m.put("businessKey", ci.getBusinessKey());
                    m.put("startUserId", ci.getStartUserId());
                    m.put("startTime", ci.getStartTime());
                    return m;
                })
                .toList();
    }

    @PostMapping("/case-instances")
    @Operation(summary = "启动案例实例")
    public Map<String, Object> startCase(
            @Parameter(description = "案例定义Key") @RequestParam String caseDefinitionKey,
            @RequestBody(required = false) Map<String, Object> variables) {
        CaseInstance ci;
        if (variables != null && !variables.isEmpty()) {
            ci = cmmnRuntimeService.createCaseInstanceBuilder()
                    .caseDefinitionKey(caseDefinitionKey)
                    .variables(variables)
                    .start();
        } else {
            ci = cmmnRuntimeService.createCaseInstanceBuilder()
                    .caseDefinitionKey(caseDefinitionKey)
                    .start();
        }
        Map<String, Object> m = new HashMap<>();
        m.put("id", ci.getId());
        m.put("caseDefinitionId", ci.getCaseDefinitionId());
        m.put("businessKey", ci.getBusinessKey());
        return m;
    }

    @GetMapping("/case-instances/{caseInstanceId}")
    @Operation(summary = "获取案例实例详情")
    public Map<String, Object> getCaseInstance(@PathVariable String caseInstanceId) {
        CaseInstance ci = cmmnRuntimeService.createCaseInstanceQuery()
                .caseInstanceId(caseInstanceId)
                .singleResult();
        if (ci == null) return Map.of();
        Map<String, Object> m = new HashMap<>();
        m.put("id", ci.getId());
        m.put("caseDefinitionId", ci.getCaseDefinitionId());
        m.put("caseDefinitionKey", ci.getCaseDefinitionKey());
        m.put("businessKey", ci.getBusinessKey());
        m.put("startUserId", ci.getStartUserId());
        return m;
    }

    @GetMapping("/tasks")
    @Operation(summary = "查询案例任务列表")
    public List<Map<String, Object>> listTasks(
            @Parameter(description = "处理人") @RequestParam(required = false) String assignee) {
        var query = cmmnTaskService.createTaskQuery();
        if (assignee != null) query.taskAssignee(assignee);
        return query.list().stream().map(t -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", t.getId());
            m.put("name", t.getName());
            m.put("assignee", t.getAssignee());
            m.put("caseInstanceId", t.getScopeId());
            m.put("createTime", t.getCreateTime());
            return m;
        }).toList();
    }

    @PostMapping("/tasks/{taskId}/complete")
    @Operation(summary = "完成案例任务")
    public Map<String, String> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) Map<String, Object> variables) {
        if (variables != null && !variables.isEmpty()) {
            cmmnTaskService.complete(taskId, variables);
        } else {
            cmmnTaskService.complete(taskId);
        }
        return Map.of("result", "completed");
    }
}
