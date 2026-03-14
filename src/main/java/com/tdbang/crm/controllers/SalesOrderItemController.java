/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tdbang.crm.commons.AuditAction;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.SalesOrderItemDTO;
import com.tdbang.crm.services.SalesOrderItemService;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/sales-order/{orderId}/item")
@Tag(name = "CRM Sales Order Item APIs")
public class SalesOrderItemController extends BaseController {

    private final SalesOrderItemService salesOrderItemService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue listOrderItems(@PathVariable Long orderId) {
        log.info("Start listOrderItems for order {}", orderId);
        ResponseDTO responseDTO = salesOrderItemService.listOrderItems(orderId);
        log.info("End listOrderItems");
        return new MappingJacksonValue(responseDTO);
    }

    @PostMapping("")
    @AuditAction(value = "ADD_ORDER_ITEM", description = "Add item to sales order")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue addOrderItem(@PathVariable Long orderId, @RequestBody @Valid SalesOrderItemDTO dto) {
        log.info("Start addOrderItem for order {}", orderId);
        ResponseDTO responseDTO = salesOrderItemService.addOrderItem(orderId, dto, getPkUserLogged());
        log.info("End addOrderItem");
        return new MappingJacksonValue(responseDTO);
    }

    @PutMapping("/{itemId}")
    @AuditAction(value = "UPDATE_ORDER_ITEM", description = "Update sales order item")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue updateOrderItem(@PathVariable Long orderId, @PathVariable Long itemId,
                                               @RequestBody @Valid SalesOrderItemDTO dto) {
        log.info("Start updateOrderItem {} for order {}", itemId, orderId);
        ResponseDTO responseDTO = salesOrderItemService.updateOrderItem(itemId, dto, getPkUserLogged());
        log.info("End updateOrderItem");
        return new MappingJacksonValue(responseDTO);
    }

    @DeleteMapping("/{itemId}")
    @AuditAction(value = "DELETE_ORDER_ITEM", description = "Delete sales order item")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public MappingJacksonValue deleteOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        log.info("Start deleteOrderItem {} for order {}", itemId, orderId);
        ResponseDTO responseDTO = salesOrderItemService.deleteOrderItem(itemId, getPkUserLogged());
        log.info("End deleteOrderItem");
        return new MappingJacksonValue(responseDTO);
    }
}
