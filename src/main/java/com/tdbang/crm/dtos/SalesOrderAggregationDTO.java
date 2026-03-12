/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Plain serializable DTO that carries the aggregated sales order statistics
 * for a single calendar day.
 *
 * <p>Populated by {@code SalesOrderAggregationTasklet} and stored in the
 * Spring Batch {@code JobExecutionContext} so that subsequent tasklets
 * ({@code ExcelGeneratorTasklet}, {@code ReportPersistenceTasklet}) can
 * consume it without re-querying the database.
 */
@Getter
@Setter
@NoArgsConstructor
public class SalesOrderAggregationDTO implements Serializable {

    private LocalDate reportDate;

    private BigDecimal totalRevenue = BigDecimal.ZERO;

    private Integer totalOrders = 0;

    private Integer ordersCreated = 0;

    private Integer ordersApproved = 0;

    private Integer ordersDelivered = 0;

    private Integer ordersCanceled = 0;
}
