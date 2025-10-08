package com.tdbang.crm.controllers;

import java.util.List;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.SalesOrderDTO;
import com.tdbang.crm.services.SalesOrderService;

@Log4j2
@RestController
@RequestMapping("/api/v1/sales-order")
public class OderController extends BaseController {

    @Autowired
    private SalesOrderService salesOrderService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue createSalesOrder(@RequestBody @Valid SalesOrderDTO salesOrderDTO) {
        log.info("Start createSalesOrder");
        ResponseDTO responseDTO = salesOrderService.createNewSalesOrder(salesOrderDTO, getPkUserLogged());
        log.info("End createSalesOrder");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveOrderDetails(@PathVariable Long id) {
        log.info("Start retrieveOrderDetails");
        ResponseDTO orderDetails = salesOrderService.getSalesOrderDetails(id);
        log.info("End retrieveOrderDetails");
        return new MappingJacksonValue(orderDetails);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue updateOrderDetails(@PathVariable Long id,
                                                  @RequestBody @Valid SalesOrderDTO salesOrderDTO) {
        log.info("Start updateOrderDetails");
        ResponseDTO responseDTO = salesOrderService.updateSalesOrderDetails(id, getPkUserLogged(), salesOrderDTO);
        log.info("End updateOrderDetails");
        return new MappingJacksonValue(responseDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue deleteOrderDetails(@PathVariable Long id) {
        log.info("Start deleteOrderDetails");
        ResponseDTO responseDTO = salesOrderService.deleteSalesOrderDetails(id, getPkUserLogged());
        log.info("End deleteOrderDetails");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveOrderList(@RequestParam(required = false) Integer pageNumber,
                                                 @RequestParam(required = false) Integer pageSize) {
        log.info("Start retrieveOrderList");
        ResponseDTO listOfOrder = salesOrderService.getListOfOrder(pageNumber, pageSize, null);
        log.info("End retrieveOrderList");
        return new MappingJacksonValue(listOfOrder);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveOrderListWithFilter(@RequestParam(required = false) Integer pageNumber,
                                                           @RequestParam(required = false) Integer pageSize,
                                                           @RequestParam(required = false) String subject) {
        log.info("Start retrieveOrderListWithFilter");
        ResponseDTO listOfOrder = salesOrderService.getListOfOrder(pageNumber, pageSize, subject);
        log.info("End retrieveOrderListWithFilter");
        return new MappingJacksonValue(listOfOrder);
    }

    @GetMapping("/count/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveOrderDashboardByStatus() {
        log.info("Start retrieveOrderDashboardByStatus");
        ResponseDTO responseDTO = salesOrderService.retrieveOrderDashboardByStatus();
        log.info("End retrieveOrderDashboardByStatus");
        return new MappingJacksonValue(responseDTO);
    }

    @PostMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue deleteSaleOrders(@RequestBody List<Long> ids) {
        log.info("Start deleteSaleOrders");
        ResponseDTO responseDTO = salesOrderService.deleteSaleOrders(ids, getPkUserLogged());
        log.info("End deleteSaleOrders");
        return new MappingJacksonValue(responseDTO);
    }
}
