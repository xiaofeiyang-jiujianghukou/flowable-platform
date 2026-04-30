package com.flowable.usercenter.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "flowable-server")
public interface FlowableClient {

    @GetMapping("/api/process/definitions")
    List<Map<String, Object>> listDefinitions();

    @GetMapping("/api/process/definitions/key/{key}")
    Map<String, Object> getDefinitionByKey(@PathVariable String key);

    @GetMapping("/api/process/definitions/{definitionId}/tasks")
    List<Map<String, Object>> getDefinitionTasks(@PathVariable String definitionId);

    @PostMapping("/api/process/instances")
    Map<String, Object> startProcess(@RequestParam String processDefinitionKey,
                                     @RequestBody Map<String, Object> vars);

    @GetMapping("/api/tasks")
    List<Map<String, Object>> listTasks(@RequestParam String assignee);

    @PostMapping("/api/tasks/{taskId}/complete")
    void completeTask(@PathVariable String taskId, @RequestBody Map<String, Object> vars);

    @GetMapping("/api/history/tasks")
    List<Map<String, Object>> historyTasks();
}
