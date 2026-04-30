package org.flowable.engine.spring.configurator;

import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.engine.cfg.AbstractProcessEngineConfigurator;
import org.flowable.engine.configurator.ProcessEngineConfigurator;
import org.flowable.spring.SpringProcessEngineConfiguration;

public class SpringProcessEngineConfigurator extends AbstractProcessEngineConfigurator {

    protected SpringProcessEngineConfiguration processEngineConfiguration;

    public SpringProcessEngineConfigurator() {
    }

    public SpringProcessEngineConfigurator setProcessEngineConfiguration(
            SpringProcessEngineConfiguration processEngineConfiguration) {
        this.processEngineConfiguration = processEngineConfiguration;
        return this;
    }

    @Override
    public void beforeInit(AbstractEngineConfiguration config) {
        if (processEngineConfiguration != null) {
            processEngineConfiguration.setDisableIdmEngine(true);
            processEngineConfiguration.setDisableEventRegistry(true);
        }
    }

    @Override
    public void configure(AbstractEngineConfiguration config) {
    }
}
