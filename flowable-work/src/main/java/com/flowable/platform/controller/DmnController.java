package com.flowable.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.flowable.dmn.api.DmnDecision;
import org.flowable.dmn.api.DmnDecisionService;
import org.flowable.dmn.api.DmnRepositoryService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dmn")
@Tag(name = "DMN 决策引擎", description = "决策表管理与执行")
public class DmnController {

    private final DmnRepositoryService dmnRepositoryService;
    private final DmnDecisionService dmnDecisionService;

    public DmnController(DmnRepositoryService dmnRepositoryService,
                         DmnDecisionService dmnDecisionService) {
        this.dmnRepositoryService = dmnRepositoryService;
        this.dmnDecisionService = dmnDecisionService;
    }

    @GetMapping("/decisions")
    @Operation(summary = "查询决策定义列表")
    public List<Map<String, Object>> listDecisions() {
        return dmnRepositoryService.createDecisionQuery()
                .latestVersion()
                .list()
                .stream()
                .map(d -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", d.getId());
                    m.put("key", d.getKey());
                    m.put("name", d.getName());
                    m.put("version", d.getVersion());
                    m.put("deploymentId", d.getDeploymentId());
                    m.put("decisionType", d.getDecisionType());
                    return m;
                })
                .toList();
    }

    @GetMapping("/decisions/{decisionId}")
    @Operation(summary = "获取决策定义详情")
    public Map<String, Object> getDecision(@PathVariable String decisionId) {
        DmnDecision d = dmnRepositoryService.getDecision(decisionId);
        if (d == null) return Map.of();
        Map<String, Object> m = new HashMap<>();
        m.put("id", d.getId());
        m.put("key", d.getKey());
        m.put("name", d.getName());
        m.put("version", d.getVersion());
        m.put("decisionType", d.getDecisionType());
        m.put("description", d.getDescription());
        return m;
    }

    @PostMapping("/execute")
    @Operation(summary = "执行决策表")
    public Map<String, Object> execute(
            @Parameter(description = "决策Key") @RequestParam String decisionKey,
            @RequestBody Map<String, Object> variables) {
        return dmnDecisionService.createExecuteDecisionBuilder()
                .decisionKey(decisionKey)
                .variables(variables)
                .executeWithSingleResult();
    }
}
