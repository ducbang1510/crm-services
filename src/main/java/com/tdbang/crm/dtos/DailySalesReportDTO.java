/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DailySalesReportDTO {
    private Long pk;
    private LocalDate reportDate;
    private BigDecimal totalRevenue;
    private Integer totalOrders;
    private Integer ordersCreated;
    private Integer ordersApproved;
    private Integer ordersDelivered;
    private Integer ordersCanceled;
    private String mongoFileId;
    private String fileName;
    private String status;
    private Date createdOn;
}
