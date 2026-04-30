package com.flowable.usercenter.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("process_config")
public class ProcessConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String processDefinitionKey;
    private String processDefinitionId;
    private String processName;
    private String taskAssignees;
    private Boolean published;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
