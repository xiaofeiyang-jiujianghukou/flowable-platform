package com.flowable.usercenter.entity;

import lombok.Data;
import java.util.List;

@Data
public class SessionUser {
    private Long userId;
    private String username;
    private String name;
    private Long departmentId;
    private String deptName;
    private List<String> roles;
    private List<String> permissions;
    private List<SysMenu> menus;
}
