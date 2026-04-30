package com.flowable.platform.service;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("approvalService")
public class ApprovalService implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(ApprovalService.class);

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        Boolean approved = (Boolean) execution.getVariable("approved");

        log.info("流程实例 {} 审批结果: {}", processInstanceId,
                Boolean.TRUE.equals(approved) ? "通过" : "驳回");

        // 更新业务记录标识
        execution.setVariable("approvalStatus",
                Boolean.TRUE.equals(approved) ? "APPROVED" : "REJECTED");
    }

    public void sendNotification(String assignee, String taskName) {
        log.info("向 {} 发送待办通知: {}", assignee, taskName);
    }
}
