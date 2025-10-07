package com.tdbang.crm.controllers.v1;

import java.util.List;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.tdbang.crm.controllers.BaseController;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.dtos.SalesOrderDTO;
import com.tdbang.crm.services.SalesOrderService;

@RestController
@RequestMapping("/api/v1/sales-order")
public class OderController extends BaseController {
    private static Logger LOGGER = LoggerFactory.getLogger(OderController.class);

    @Autowired
    private SalesOrderService salesOrderService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue createSalesOrder(@RequestBody @Valid SalesOrderDTO salesOrderDTO) {
        LOGGER.info("Start createSalesOrder");
        ResponseDTO responseDTO = salesOrderService.createNewSalesOrder(salesOrderDTO, getPkUserLogged());
        LOGGER.info("End createSalesOrder");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveOrderDetails(@PathVariable Long id) {
        LOGGER.info("Start retrieveOrderDetails");
        ResponseDTO orderDetails = salesOrderService.getSalesOrderDetails(id);
        LOGGER.info("End retrieveOrderDetails");
        return new MappingJacksonValue(orderDetails);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue updateOrderDetails(@PathVariable Long id,
                                                  @RequestBody @Valid SalesOrderDTO salesOrderDTO) {
        LOGGER.info("Start updateOrderDetails");
        ResponseDTO responseDTO = salesOrderService.updateSalesOrderDetails(id, getPkUserLogged(), salesOrderDTO);
        LOGGER.info("End updateOrderDetails");
        return new MappingJacksonValue(responseDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue deleteOrderDetails(@PathVariable Long id) {
        LOGGER.info("Start deleteOrderDetails");
        ResponseDTO responseDTO = salesOrderService.deleteSalesOrderDetails(id, getPkUserLogged());
        LOGGER.info("End deleteOrderDetails");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveOrderList(@RequestParam(required = false) Integer pageNumber,
                                                 @RequestParam(required = false) Integer pageSize) {
        LOGGER.info("Start retrieveOrderList");
        ResponseDTO listOfOrder = salesOrderService.getListOfOrder(pageNumber, pageSize, null);
        LOGGER.info("End retrieveOrderList");
        return new MappingJacksonValue(listOfOrder);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveOrderListWithFilter(@RequestParam(required = false) Integer pageNumber,
                                                           @RequestParam(required = false) Integer pageSize,
                                                           @RequestParam(required = false) String subject) {
        LOGGER.info("Start retrieveOrderListWithFilter");
        ResponseDTO listOfOrder = salesOrderService.getListOfOrder(pageNumber, pageSize, subject);
        LOGGER.info("End retrieveOrderListWithFilter");
        return new MappingJacksonValue(listOfOrder);
    }

    @GetMapping("/count/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue retrieveOrderDashboardByStatus() {
        LOGGER.info("Start retrieveOrderDashboardByStatus");
        ResponseDTO responseDTO = salesOrderService.retrieveOrderDashboardByStatus();
        LOGGER.info("End retrieveOrderDashboardByStatus");
        return new MappingJacksonValue(responseDTO);
    }

    @PostMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN', 'USER')")
    public MappingJacksonValue deleteSaleOrders(@RequestBody List<Long> ids) {
        LOGGER.info("Start deleteSaleOrders");
        ResponseDTO responseDTO = salesOrderService.deleteSaleOrders(ids, getPkUserLogged());
        LOGGER.info("End deleteSaleOrders");
        return new MappingJacksonValue(responseDTO);
    }
}
