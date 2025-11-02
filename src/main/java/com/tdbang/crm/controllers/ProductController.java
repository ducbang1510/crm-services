/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import java.util.List;

import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tdbang.crm.commons.AuditAction;
import com.tdbang.crm.dtos.ProductDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.services.ProductService;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/product")
@Tag(name = "CRM Product APIs")
public class ProductController extends BaseController {

    private final ProductService productService;

    @PostMapping("")
    @AuditAction(value = "CREATE_PRODUCT", description = "Create new product")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue createProduct(@RequestBody @Valid ProductDTO productDTO) {
        log.info("Start createProduct");
        ResponseDTO responseDTO = productService.createNewProduct(productDTO);
        log.info("End createProduct");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF', 'USER')")
    public MappingJacksonValue retrieveProductDetails(@PathVariable Long id) {
        log.info("Start retrieveProductDetails");
        ResponseDTO orderDetails = productService.getProductDetails(id);
        log.info("End retrieveProductDetails");
        return new MappingJacksonValue(orderDetails);
    }

    @PutMapping("/{id}")
    @AuditAction(value = "UPDATE_PRODUCT", description = "Update existing product")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue updateProductDetails(@PathVariable Long id,
                                                    @RequestBody @Valid ProductDTO productDTO) {
        log.info("Start updateProductDetails");
        ResponseDTO responseDTO = productService.updateProductDetails(id, getPkUserLogged(), productDTO);
        log.info("End updateProductDetails");
        return new MappingJacksonValue(responseDTO);
    }

    @DeleteMapping("/{id}")
    @AuditAction(value = "DELETE_PRODUCT", description = "Delete existing product")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue deleteProductDetails(@PathVariable Long id) {
        log.info("Start deleteProductDetails");
        ResponseDTO responseDTO = productService.deleteProducts(List.of(id));
        log.info("End deleteProductDetails");
        return new MappingJacksonValue(responseDTO);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF', 'USER')")
    public MappingJacksonValue retrieveProductList(
        @RequestParam(required = false) @Parameter(description = "Optional filter on fields", example = "name:*Monitor*") String filter,
        @RequestParam(required = false) @Parameter(description = "Optional fields to be included in the response", example = "pk,name") String fields,
        @RequestParam(required = false, defaultValue = "0") int pageNumber,
        @RequestParam(required = false, defaultValue = "0") int pageSize,
        @RequestParam(required = false, defaultValue = "pk") String sortColumn,
        @RequestParam(required = false, defaultValue = "ASC") String sortOrder) {
        log.info("Start retrieveProductList");
        ResponseDTO listOfProduct = productService.getListOfProduct(filter, pageSize, pageNumber, sortColumn, sortOrder, fields);
        log.info("End retrieveProductList");
        return new MappingJacksonValue(listOfProduct);
    }

    @PostMapping("/delete")
    @AuditAction(value = "DELETE_PRODUCTS", description = "Delete existing products")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('STAFF')")
    public MappingJacksonValue deleteProducts(@RequestBody List<Long> ids) {
        log.info("Start deleteProducts");
        ResponseDTO responseDTO = productService.deleteProducts(ids);
        log.info("End deleteProducts");
        return new MappingJacksonValue(responseDTO);
    }
}
