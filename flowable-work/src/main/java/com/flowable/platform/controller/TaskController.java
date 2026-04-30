package com.flowable.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.engine.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "任务管理", description = "待办任务、已办任务、任务认领与完成")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "查询任务列表")
    public List<Map<String, Object>> list(
            @Parameter(description = "处理人") @RequestParam(required = false) String assignee,
            @Parameter(description = "候选组") @RequestParam(required = false) String candidateGroup) {
        TaskQuery query = taskService.createTaskQuery();
        if (assignee != null) query.taskAssignee(assignee);
        if (candidateGroup != null) query.taskCandidateGroup(candidateGroup);
        return query.list().stream().map(t -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", t.getId());
            m.put("name", t.getName());
            m.put("description", t.getDescription());
            m.put("assignee", t.getAssignee());
            m.put("processInstanceId", t.getProcessInstanceId());
            m.put("processDefinitionId", t.getProcessDefinitionId());
            m.put("createTime", t.getCreateTime());
            m.put("dueDate", t.getDueDate());
            return m;
        }).toList();
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "获取任务详情")
    public Map<String, Object> getById(@PathVariable String taskId) {
        Task t = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (t == null) return Map.of();
        Map<String, Object> variables = taskService.getVariables(taskId);
        Map<String, Object> m = new HashMap<>();
        m.put("id", t.getId());
        m.put("name", t.getName());
        m.put("assignee", t.getAssignee());
        m.put("processInstanceId", t.getProcessInstanceId());
        m.put("createTime", t.getCreateTime());
        m.put("variables", variables);
        return m;
    }

    @PostMapping("/{taskId}/complete")
    @Operation(summary = "完成任务")
    public Map<String, String> complete(
            @PathVariable String taskId,
            @RequestBody(required = false) Map<String, Object> variables) {
        if (variables != null && !variables.isEmpty()) {
            taskService.complete(taskId, variables);
        } else {
            taskService.complete(taskId);
        }
        return Map.of("result", "completed");
    }

    @PostMapping("/{taskId}/claim")
    @Operation(summary = "认领任务")
    public Map<String, String> claim(
            @PathVariable String taskId,
            @Parameter(description = "认领人") @RequestParam String userId) {
        taskService.claim(taskId, userId);
        return Map.of("result", "claimed");
    }
}
