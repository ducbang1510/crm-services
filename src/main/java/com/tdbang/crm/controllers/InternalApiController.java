/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.services.ContactService;
import com.tdbang.crm.services.DashboardService;
import com.tdbang.crm.services.NoteService;
import com.tdbang.crm.services.ProductService;
import com.tdbang.crm.services.ReportService;
import com.tdbang.crm.services.SalesOrderItemService;
import com.tdbang.crm.services.SalesOrderService;
import com.tdbang.crm.services.TaskService;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/v1")
@Hidden
public class InternalApiController {

    private final DashboardService dashboardService;
    private final ReportService reportService;
    private final TaskService taskService;
    private final ContactService contactService;
    private final SalesOrderService salesOrderService;
    private final SalesOrderItemService salesOrderItemService;
    private final NoteService noteService;
    private final ProductService productService;

    // Dashboard

    @GetMapping("/dashboard/revenue-trend")
    @ResponseStatus(HttpStatus.OK)
    public MappingJacksonValue getRevenueTrend(@RequestParam(defaultValue = "12") int months) {
        log.info("Internal API: getRevenueTrend months={}", months);
        ResponseDTO responseDTO = dashboardService.getRevenueTrend(months);
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/dashboard/pipeline-summary")
    @ResponseStatus(HttpStatus.OK)
    public MappingJacksonValue getPipelineSummary() {
        log.info("Internal API: getPipelineSummary");
        ResponseDTO responseDTO = dashboardService.getPipelineSummary();
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/dashboard/top-users")
    @ResponseStatus(HttpStatus.OK)
    public MappingJacksonValue getTopUsers(@RequestParam(defaultValue = "5") int limit) {
        log.info("Internal API: getTopUsers limit={}", limit);
        ResponseDTO responseDTO = dashboardService.getTopUsers(limit);
        return new MappingJacksonValue(responseDTO);
    }

    // Reports

    @GetMapping("/reports")
    @ResponseStatus(HttpStatus.OK)
    public MappingJacksonValue listReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        log.info("Internal API: listReports from={} to={}", from, to);
        ResponseDTO responseDTO = reportService.listReports(from, to);
        return new MappingJacksonValue(responseDTO);
    }

    // Tasks

    @GetMapping("/tasks/summary")
    @ResponseStatus(HttpStatus.OK)
    public MappingJacksonValue getTaskSummary(@RequestParam Long userPk) {
        log.info("Internal API: getTaskSummary userPk={}", userPk);
        ResponseDTO responseDTO = taskService.getTaskSummary(userPk);
        return new MappingJacksonValue(responseDTO);
    }

    // Contacts

    @GetMapping("/contacts")
    @ResponseStatus(HttpStatus.OK)
    public MappingJacksonValue searchContacts(
            @RequestParam(required = false) String contactName,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("Internal API: searchContacts contactName={}", contactName);
        ResponseDTO responseDTO = contactService.getListOfContactWithNonDynamicFilter(pageNumber, pageSize, contactName);
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/contacts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MappingJacksonValue getContactDetails(@PathVariable Long id) {
        log.info("Internal API: getContactDetails id={}", id);
        ResponseDTO responseDTO = contactService.getContactDetails(id);
        return new MappingJacksonValue(responseDTO);
    }

    // Sales Orders

    @GetMapping("/sales-orders")
    @ResponseStatus(HttpStatus.OK)
    public MappingJacksonValue searchOrders(
            @RequestParam(required = false) String subject,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("Internal API: searchOrders subject={}", subject);
        ResponseDTO responseDTO = salesOrderService.retrieveOrderListWithNonDynamicFilter(pageNumber, pageSize, subject);
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/sales-orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MappingJacksonValue getOrderDetails(@PathVariable Long id) {
        log.info("Internal API: getOrderDetails id={}", id);
        ResponseDTO responseDTO = salesOrderService.getSalesOrderDetails(id);
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/sales-orders/count/status")
    @ResponseStatus(HttpStatus.OK)
    public MappingJacksonValue getOrderStatusCounts() {
        log.info("Internal API: getOrderStatusCounts");
        ResponseDTO responseDTO = salesOrderService.retrieveOrderDashboardByStatus();
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/sales-orders/{orderId}/items")
    @ResponseStatus(HttpStatus.OK)
    public MappingJacksonValue getOrderItems(@PathVariable Long orderId) {
        log.info("Internal API: getOrderItems orderId={}", orderId);
        ResponseDTO responseDTO = salesOrderItemService.listOrderItems(orderId);
        return new MappingJacksonValue(responseDTO);
    }

    // Notes

    @GetMapping("/notes")
    @ResponseStatus(HttpStatus.OK)
    public MappingJacksonValue listNotes(
            @RequestParam String entityType,
            @RequestParam Long entityFk) {
        log.info("Internal API: listNotes entityType={} entityFk={}", entityType, entityFk);
        ResponseDTO responseDTO = noteService.listNotes(entityType, entityFk);
        return new MappingJacksonValue(responseDTO);
    }

    // Products

    @GetMapping("/products")
    @ResponseStatus(HttpStatus.OK)
    public MappingJacksonValue listProducts(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "50") int pageSize) {
        log.info("Internal API: listProducts pageSize={}", pageSize);
        ResponseDTO responseDTO = productService.getListOfProduct(null, pageSize, pageNumber, "pk", "ASC", null);
        return new MappingJacksonValue(responseDTO);
    }
}
