/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tdbang.crm.dtos.DailySalesReportDTO;
import com.tdbang.crm.entities.DailySalesReport;

@Component
public class DailySalesReportMapper {

    public DailySalesReportDTO mappingEntityToDTO(DailySalesReport report) {
        DailySalesReportDTO dto = new DailySalesReportDTO();
        dto.setPk(report.getPk());
        dto.setReportDate(report.getReportDate());
        dto.setTotalRevenue(report.getTotalRevenue());
        dto.setTotalOrders(report.getTotalOrders());
        dto.setOrdersCreated(report.getOrdersCreated());
        dto.setOrdersApproved(report.getOrdersApproved());
        dto.setOrdersDelivered(report.getOrdersDelivered());
        dto.setOrdersCanceled(report.getOrdersCanceled());
        dto.setMongoFileId(report.getMongoFileId());
        dto.setFileName(report.getFileName());
        dto.setStatus(report.getStatus() != null ? report.getStatus().name() : null);
        dto.setCreatedOn(report.getCreatedOn());
        return dto;
    }

    public List<DailySalesReportDTO> mappingToListDTO(List<DailySalesReport> reports) {
        return reports.stream().map(this::mappingEntityToDTO).toList();
    }
}
