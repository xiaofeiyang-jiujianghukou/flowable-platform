package org.flowable.dmn.spring.configurator;

import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.dmn.engine.DmnEngineConfiguration;
import org.flowable.dmn.engine.configurator.DmnEngineConfigurator;

public class SpringDmnEngineConfigurator implements DmnEngineConfigurator {

    protected DmnEngineConfiguration dmnEngineConfiguration;

    public SpringDmnEngineConfigurator() {
    }

    public DmnEngineConfigurator setDmnEngineConfiguration(
            DmnEngineConfiguration dmnEngineConfiguration) {
        this.dmnEngineConfiguration = dmnEngineConfiguration;
        return this;
    }

    @Override
    public void beforeInit(AbstractEngineConfiguration config) {
    }

    @Override
    public void configure(AbstractEngineConfiguration config) {
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
