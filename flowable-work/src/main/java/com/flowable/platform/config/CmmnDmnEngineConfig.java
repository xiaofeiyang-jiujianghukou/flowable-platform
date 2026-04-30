package com.flowable.platform.config;

import javax.sql.DataSource;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.cmmn.spring.SpringCmmnEngineConfiguration;
import org.flowable.dmn.engine.DmnEngine;
import org.flowable.dmn.spring.SpringDmnEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CmmnDmnEngineConfig {

    @Bean
    public SpringCmmnEngineConfiguration cmmnEngineConfiguration(
            DataSource dataSource, PlatformTransactionManager transactionManager) {
        SpringCmmnEngineConfiguration config = new SpringCmmnEngineConfiguration();
        config.setDataSource(dataSource);
        config.setTransactionManager(transactionManager);
        config.setDatabaseSchemaUpdate("true");
        return config;
    }

    @Bean
    public CmmnEngine cmmnEngine(SpringCmmnEngineConfiguration config) {
        return config.buildCmmnEngine();
    }

    @Bean
    public SpringDmnEngineConfiguration dmnEngineConfiguration(
            DataSource dataSource, PlatformTransactionManager transactionManager) {
        SpringDmnEngineConfiguration config = new SpringDmnEngineConfiguration();
        config.setDataSource(dataSource);
        config.setTransactionManager(transactionManager);
        config.setDatabaseSchemaUpdate("true");
        return config;
    }

    @Bean
    public DmnEngine dmnEngine(SpringDmnEngineConfiguration config) {
        return config.buildDmnEngine();
    }
}
