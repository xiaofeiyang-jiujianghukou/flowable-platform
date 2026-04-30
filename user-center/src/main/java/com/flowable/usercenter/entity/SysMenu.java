package com.flowable.usercenter.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_menu")
public class SysMenu {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String path;
    private String icon;
    private Long parentId;
    private String permissionCode;
    private Integer sortOrder;
    private String type;
    @JsonIgnore
    private LocalDateTime createdAt;
    @TableField(exist = false)
    private List<SysMenu> children;
}
