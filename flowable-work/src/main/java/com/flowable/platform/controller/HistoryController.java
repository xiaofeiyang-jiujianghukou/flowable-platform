package com.flowable.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
@Tag(name = "历史记录", description = "流程历史与审计日志")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/process-instances")
    @Operation(summary = "查询历史流程实例")
    public List<Map<String, Object>> listProcessInstances(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        return historyService.createHistoricProcessInstanceQuery()
                .orderByProcessInstanceEndTime().desc()
                .listPage(offset, limit)
                .stream()
                .map(hpi -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", hpi.getId());
                    m.put("processDefinitionId", hpi.getProcessDefinitionId());
                    m.put("processDefinitionKey", hpi.getProcessDefinitionKey());
                    m.put("processDefinitionName", hpi.getProcessDefinitionName());
                    m.put("businessKey", hpi.getBusinessKey());
                    m.put("startTime", hpi.getStartTime());
                    m.put("endTime", hpi.getEndTime());
                    m.put("durationInMillis", hpi.getDurationInMillis());
                    m.put("startUserId", hpi.getStartUserId());
                    m.put("deleteReason", hpi.getDeleteReason());
                    return m;
                })
                .toList();
    }

    @GetMapping("/process-instances/{processInstanceId}")
    @Operation(summary = "获取历史流程实例详情")
    public Map<String, Object> getById(@PathVariable String processInstanceId) {
        HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (hpi == null) return Map.of();
        Map<String, Object> m = new HashMap<>();
        m.put("id", hpi.getId());
        m.put("processDefinitionId", hpi.getProcessDefinitionId());
        m.put("businessKey", hpi.getBusinessKey());
        m.put("startTime", hpi.getStartTime());
        m.put("endTime", hpi.getEndTime());
        m.put("durationInMillis", hpi.getDurationInMillis());
        m.put("startUserId", hpi.getStartUserId());
        return m;
    }

    @GetMapping("/tasks")
    @Operation(summary = "查询历史任务实例")
    public List<Map<String, Object>> listTaskInstances(
            @RequestParam(required = false) String processInstanceId) {
        var query = historyService.createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceEndTime().desc();
        if (processInstanceId != null) {
            query.processInstanceId(processInstanceId);
        }
        return query.listPage(0, 50).stream().map(hti -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", hti.getId());
            m.put("name", hti.getName());
            m.put("assignee", hti.getAssignee());
            m.put("processInstanceId", hti.getProcessInstanceId());
            m.put("startTime", hti.getCreateTime());
            m.put("endTime", hti.getEndTime());
            m.put("durationInMillis", hti.getDurationInMillis());
            return m;
        }).toList();
    }
}
