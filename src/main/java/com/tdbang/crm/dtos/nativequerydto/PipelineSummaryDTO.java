/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.dtos.nativequerydto;

import java.math.BigDecimal;

public interface PipelineSummaryDTO {

    Integer getStatus();

    Long getOrderCount();

    BigDecimal getTotalRevenue();
}
