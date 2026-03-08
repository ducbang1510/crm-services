/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.dtos.nativequerydto;

import java.math.BigDecimal;

/**
 * JPA interface for the daily sales order aggregation native query.
 */
public interface SalesOrderDailyAggregationDTO {

    Long getTotalOrders();

    BigDecimal getTotalRevenue();

    Integer getOrdersCreated();

    Integer getOrdersApproved();

    Integer getOrdersDelivered();

    Integer getOrdersCanceled();
}
