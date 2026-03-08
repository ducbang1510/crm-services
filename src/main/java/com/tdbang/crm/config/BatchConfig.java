/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Batch configuration.
 *
 * <p>This class activates {@link CrmBatchProperties} for type-safe binding of
 * all batch job configuration under the "crm.batch.*" property namespace.
 */
@Configuration
@EnableConfigurationProperties(CrmBatchProperties.class)
public class BatchConfig {
}
