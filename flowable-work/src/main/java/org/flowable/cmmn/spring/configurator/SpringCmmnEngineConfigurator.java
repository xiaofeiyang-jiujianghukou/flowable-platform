package org.flowable.cmmn.spring.configurator;

import org.flowable.cmmn.engine.configurator.CmmnEngineConfigurator;
import org.flowable.cmmn.spring.SpringCmmnEngineConfiguration;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;

public class SpringCmmnEngineConfigurator implements CmmnEngineConfigurator {

    protected SpringCmmnEngineConfiguration cmmnEngineConfiguration;

    public SpringCmmnEngineConfigurator() {
    }

    public SpringCmmnEngineConfigurator setCmmnEngineConfiguration(
            SpringCmmnEngineConfiguration cmmnEngineConfiguration) {
        this.cmmnEngineConfiguration = cmmnEngineConfiguration;
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
