package com.tdbang.crm.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tdbang.crm.services.ProductService;

@Log4j2
@RestController
@RequestMapping("/api/v1/product")
@Tag(name = "CRM Contact APIs")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;
}
