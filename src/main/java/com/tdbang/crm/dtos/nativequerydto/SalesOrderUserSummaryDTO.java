/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.dtos.nativequerydto;

import java.math.BigDecimal;

/**
 * JPA interface for the per-user sales order summary native query.
 */
public interface SalesOrderUserSummaryDTO {

    String getAssignedToName();

    Long getOrderCount();

    BigDecimal getTotalRevenue();
}
