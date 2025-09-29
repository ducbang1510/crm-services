package com.tdbang.crm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tdbang.crm.services.SalesOrderService;

@RestController
@RequestMapping("/api/v1/order")
public class OderController {
    private static Logger LOGGER = LoggerFactory.getLogger(OderController.class);

    @Autowired
    private SalesOrderService salesOrderService;
}
