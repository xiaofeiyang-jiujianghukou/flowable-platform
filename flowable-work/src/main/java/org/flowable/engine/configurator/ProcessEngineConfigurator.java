package org.flowable.engine.configurator;

import org.flowable.common.engine.impl.EngineConfigurator;

/**
 * Flowable 8.0.0 补丁：该接口在 8.0.0 中被移除，
 * 但 flowable-spring-boot-autoconfigure 仍引用它。
 */
public interface ProcessEngineConfigurator extends EngineConfigurator {
}
