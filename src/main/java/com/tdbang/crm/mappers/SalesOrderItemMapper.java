/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tdbang.crm.dtos.SalesOrderItemDTO;
import com.tdbang.crm.entities.SalesOrderItem;

@Component
public class SalesOrderItemMapper {

    public SalesOrderItemDTO mappingEntityToDTO(SalesOrderItem item) {
        SalesOrderItemDTO dto = new SalesOrderItemDTO();
        dto.setPk(item.getPk());
        dto.setSalesOrderFk(item.getSalesOrder().getPk());
        dto.setProductFk(item.getProduct().getPk());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setDiscount(item.getDiscount());
        dto.setLineTotal(item.getLineTotal());
        dto.setSortOrder(item.getSortOrder());
        dto.setCreatedOn(item.getCreatedOn());
        dto.setUpdatedOn(item.getUpdatedOn());
        return dto;
    }

    public List<SalesOrderItemDTO> mappingToListDTO(List<SalesOrderItem> items) {
        return items.stream().map(this::mappingEntityToDTO).toList();
    }
}
