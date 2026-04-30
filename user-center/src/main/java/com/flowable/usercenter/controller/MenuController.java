package com.flowable.usercenter.controller;

import com.flowable.usercenter.entity.SessionUser;
import com.flowable.usercenter.entity.SysMenu;
import com.flowable.usercenter.mapper.SysMenuMapper;
import com.flowable.usercenter.mapper.SysRoleMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/menus")
@Tag(name = "菜单管理", description = "菜单权限配置")
public class MenuController {

    private final SysMenuMapper menuMapper;
    private final SysRoleMapper roleMapper;

    public MenuController(SysMenuMapper menuMapper, SysRoleMapper roleMapper) {
        this.menuMapper = menuMapper; this.roleMapper = roleMapper;
    }

    @GetMapping("/my")
    @Operation(summary = "获取当前用户的菜单树")
    public List<SysMenu> myMenus(HttpServletRequest req) {
        var user = (SessionUser) req.getAttribute("sessionUser");
        var all = menuMapper.selectByUserId(user.getUserId());
        return buildTree(all);
    }

    @GetMapping("/my/permissions")
    @Operation(summary = "获取当前用户的权限编码列表")
    public List<String> myPermissions(HttpServletRequest req) {
        var user = (SessionUser) req.getAttribute("sessionUser");
        return menuMapper.selectPermissions(user.getUserId());
    }

    @GetMapping
    @Operation(summary = "菜单列表（管理）")
    public List<SysMenu> list() { return menuMapper.selectList(null); }

    @PostMapping
    @Operation(summary = "创建菜单")
    public Map<String, String> create(@RequestBody SysMenu menu) {
        menuMapper.insert(menu);
        return Map.of("result", "created");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新菜单")
    public Map<String, String> update(@PathVariable Long id, @RequestBody SysMenu menu) {
        menu.setId(id); menuMapper.updateById(menu);
        return Map.of("result", "updated");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单")
    public Map<String, String> delete(@PathVariable Long id) {
        menuMapper.deleteById(id);
        return Map.of("result", "deleted");
    }

    @GetMapping("/roles/{roleCode}")
    @Operation(summary = "获取角色拥有的菜单ID列表")
    public List<Long> roleMenus(@PathVariable String roleCode) {
        return roleMapper.selectMenuIds(roleCode);
    }

    @PutMapping("/roles/{roleCode}")
    @Operation(summary = "保存角色的菜单权限")
    public Map<String, String> saveRoleMenus(@PathVariable String roleCode, @RequestBody List<Long> menuIds) {
        roleMapper.deleteRoleMenus(roleCode);
        for (var mid : menuIds) roleMapper.insertRoleMenu(roleCode, mid);
        return Map.of("result", "saved");
    }

    private List<SysMenu> buildTree(List<SysMenu> list) {
        Map<Long, SysMenu> map = new HashMap<>();
        List<SysMenu> roots = new ArrayList<>();
        for (var m : list) { m.setChildren(new ArrayList<>()); map.put(m.getId(), m); }
        for (var m : list) {
            if (m.getParentId() != null && m.getParentId() > 0 && map.containsKey(m.getParentId())) {
                map.get(m.getParentId()).getChildren().add(m);
            } else {
                roots.add(m);
            }
        }
        return roots;
    }
}
