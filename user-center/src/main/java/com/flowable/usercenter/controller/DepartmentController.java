package com.flowable.usercenter.controller;

import com.flowable.usercenter.entity.SysDepartment;
import com.flowable.usercenter.mapper.SysDepartmentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/departments")
@Tag(name = "部门管理", description = "组织架构部门增删改查")
public class DepartmentController {

    private final SysDepartmentMapper deptMapper;

    public DepartmentController(SysDepartmentMapper deptMapper) { this.deptMapper = deptMapper; }

    @GetMapping
    @Operation(summary = "查询部门列表")
    public List<SysDepartment> tree() { return deptMapper.selectWithUserCount(); }

    @PostMapping
    @Operation(summary = "创建部门")
    public Map<String, String> create(@RequestBody SysDepartment dept) {
        deptMapper.insert(dept);
        return Map.of("result", "created");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新部门")
    public Map<String, String> update(@PathVariable Long id, @RequestBody SysDepartment dept) {
        dept.setId(id);
        deptMapper.updateById(dept);
        return Map.of("result", "updated");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    public Map<String, String> delete(@PathVariable Long id) {
        deptMapper.deleteById(id);
        return Map.of("result", "deleted");
    }
}
