package com.flowable.usercenter.controller;

import com.flowable.usercenter.entity.SysUser;
import com.flowable.usercenter.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户增删改查，支持按关键词/角色筛选")
public class UserController {

    private final SysUserMapper userMapper;

    public UserController(SysUserMapper userMapper) { this.userMapper = userMapper; }

    @GetMapping
    @Operation(summary = "查询用户列表")
    public List<SysUser> list(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "角色编码过滤") @RequestParam(required = false) String roleCode) {
        return userMapper.selectWithDept(keyword, roleCode);
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public Map<String, String> create(@RequestBody SysUser user) {
        userMapper.insert(user);
        return Map.of("result", "created");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public Map<String, String> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        userMapper.updateById(user);
        return Map.of("result", "updated");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Map<String, String> delete(@Parameter(description = "用户ID") @PathVariable Long id) {
        userMapper.deleteById(id);
        return Map.of("result", "deleted");
    }
}
