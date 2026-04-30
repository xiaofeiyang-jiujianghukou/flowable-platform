package com.flowable.usercenter.controller;

import com.flowable.usercenter.entity.SysRole;
import com.flowable.usercenter.entity.SysUser;
import com.flowable.usercenter.mapper.SysRoleMapper;
import com.flowable.usercenter.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "角色管理", description = "角色增删、用户分配")
public class RoleController {

    private final SysRoleMapper roleMapper;
    private final SysUserMapper userMapper;

    public RoleController(SysRoleMapper roleMapper, SysUserMapper userMapper) {
        this.roleMapper = roleMapper; this.userMapper = userMapper;
    }

    @GetMapping
    @Operation(summary = "查询角色列表")
    public List<SysRole> list() { return roleMapper.selectWithUserCount(); }

    @GetMapping("/{roleCode}/users")
    @Operation(summary = "查询角色下的用户")
    public List<SysUser> users(@Parameter(description = "角色编码") @PathVariable String roleCode,
                               @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        return userMapper.selectWithDept(keyword, roleCode);
    }

    @PostMapping("/{roleCode}/users/{userId}")
    @Operation(summary = "给用户分配角色")
    public Map<String, String> assignUser(
            @Parameter(description = "角色编码") @PathVariable String roleCode,
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        roleMapper.assignUser(userId, roleCode);
        return Map.of("result", "assigned");
    }

    @DeleteMapping("/{roleCode}/users/{userId}")
    @Operation(summary = "移除用户角色")
    public Map<String, String> removeUser(
            @Parameter(description = "角色编码") @PathVariable String roleCode,
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        roleMapper.removeUser(userId, roleCode);
        return Map.of("result", "removed");
    }

    @PostMapping
    @Operation(summary = "创建角色")
    public Map<String, String> create(@RequestBody SysRole role) {
        roleMapper.insert(role);
        return Map.of("result", "created");
    }

    @DeleteMapping("/{roleCode}")
    @Operation(summary = "删除角色")
    public Map<String, String> delete(@Parameter(description = "角色编码") @PathVariable String roleCode) {
        roleMapper.deleteById(roleCode);
        return Map.of("result", "deleted");
    }
}
