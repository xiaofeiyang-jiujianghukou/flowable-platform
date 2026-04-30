package com.flowable.platform.listener;

import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableProcessStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProcessEventListener extends AbstractFlowableEngineEventListener {

    private static final Logger log = LoggerFactory.getLogger(ProcessEventListener.class);

    @Override
    protected void processStarted(FlowableProcessStartedEvent event) {
        log.info("流程启动: nestedProcessInstanceId={}, variables={}",
                event.getNestedProcessInstanceId(), event.getVariables());
    }

    @Override
    protected void processCompleted(FlowableEngineEntityEvent event) {
        log.info("流程完成: processInstanceId={}", event.getProcessInstanceId());
    }

    @Override
    protected void taskCreated(FlowableEngineEntityEvent event) {
        log.info("任务创建: processInstanceId={}, entity={}",
                event.getProcessInstanceId(), event.getEntity());
    }

    @Override
    protected void taskCompleted(FlowableEngineEntityEvent event) {
        log.info("任务完成: processInstanceId={}, entity={}",
                event.getProcessInstanceId(), event.getEntity());
    }
}
