package com.flowable.usercenter.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowable.usercenter.entity.SessionUser;
import com.flowable.usercenter.entity.SysMenu;
import com.flowable.usercenter.entity.SysUser;
import com.flowable.usercenter.mapper.SysDepartmentMapper;
import com.flowable.usercenter.mapper.SysMenuMapper;
import com.flowable.usercenter.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "登录、登出、会话")
public class AuthController {

    private static final String TOKEN_PREFIX = "session:";
    private static final long EXPIRE_HOURS = 2;
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final SysUserMapper userMapper;
    private final SysDepartmentMapper deptMapper;
    private final SysMenuMapper menuMapper;
    private final StringRedisTemplate redis;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SecureRandom random = new SecureRandom();

    public AuthController(SysUserMapper userMapper, SysDepartmentMapper deptMapper,
                          SysMenuMapper menuMapper, StringRedisTemplate redis) {
        this.userMapper = userMapper;
        this.deptMapper = deptMapper;
        this.menuMapper = menuMapper;
        this.redis = redis;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Map<String, Object> login(@RequestBody Map<String, String> body) throws JsonProcessingException {
        String username = body.get("username");
        String password = body.get("password");

        var user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user == null || !encoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        String token = generateToken();
        SessionUser session = buildSession(user);
        redis.opsForValue().set(TOKEN_PREFIX + token, objectMapper.writeValueAsString(session), EXPIRE_HOURS, TimeUnit.HOURS);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", session);
        return result;
    }

    @PostMapping("/logout")
    @Operation(summary = "登出")
    public Map<String, String> logout(HttpServletRequest req) {
        String token = extractToken(req);
        if (token != null) redis.delete(TOKEN_PREFIX + token);
        return Map.of("result", "logged_out");
    }

    @GetMapping("/session")
    @Operation(summary = "获取当前会话")
    public SessionUser session(HttpServletRequest req) {
        return (SessionUser) req.getAttribute("sessionUser");
    }

    private String extractToken(HttpServletRequest req) {
        String t = req.getHeader("Authorization");
        if (t != null && t.startsWith("Bearer ")) t = t.substring(7);
        return t;
    }

    private String generateToken() {
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < 32; i++) sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        return sb.toString();
    }

    private SessionUser buildSession(SysUser user) {
        SessionUser s = new SessionUser();
        s.setUserId(user.getId());
        s.setUsername(user.getUsername());
        s.setName(user.getName());
        s.setDepartmentId(user.getDepartmentId());
        if (user.getDepartmentId() != null) {
            var dept = deptMapper.selectById(user.getDepartmentId());
            if (dept != null) s.setDeptName(dept.getName());
        }
        var roles = userMapper.selectRoleCodes(user.getId());
        s.setRoles(roles);
        s.setPermissions(menuMapper.selectPermissions(user.getId()));
        // 构建菜单树
        var menus = menuMapper.selectByUserId(user.getId());
        s.setMenus(buildMenuTree(menus));
        return s;
    }

    private List<SysMenu> buildMenuTree(List<SysMenu> list) {
        Map<Long, SysMenu> map = new java.util.HashMap<>();
        List<SysMenu> roots = new java.util.ArrayList<>();
        for (var m : list) { m.setChildren(new java.util.ArrayList<>()); map.put(m.getId(), m); }
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
