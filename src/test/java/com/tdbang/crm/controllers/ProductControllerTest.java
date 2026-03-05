/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.controllers;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tdbang.crm.dtos.ProductDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.services.ProductService;
import com.tdbang.crm.services.SecurityService;
import com.tdbang.crm.services.UserService;
import com.tdbang.crm.utils.MessageConstants;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = ProductController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private UserService userService;

    @Test
    void createProduct_withValidBody_returnsCreated() throws Exception {
        ProductDTO dto = buildProductDTO();
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CREATING_NEW_PRODUCT_SUCCESS);

        when(productService.createNewProduct(any(ProductDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());
    }

    @Test
    void createProduct_withMissingRequiredField_returnsBadRequest() throws Exception {
        ProductDTO dto = new ProductDTO();

        mockMvc.perform(post("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void retrieveProductDetails_withValidId_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_CONTACT_SUCCESS);
        when(productService.getProductDetails(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/product/1"))
            .andExpect(status().isOk());
    }

    @Test
    void updateProductDetails_withValidBody_returnsOk() throws Exception {
        ProductDTO dto = buildProductDTO();
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_PRODUCT_SUCCESS);

        when(securityService.getCurrentUsername()).thenReturn("testuser");
        when(userService.getUserPkByUsername("testuser")).thenReturn(1L);
        when(productService.updateProductDetails(anyLong(), anyLong(), any(ProductDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/product/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }

    @Test
    void deleteProductDetails_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_LIST_OF_PRODUCTS_SUCCESS);
        when(productService.deleteProducts(List.of(1L))).thenReturn(response);

        mockMvc.perform(delete("/api/v1/product/1"))
            .andExpect(status().isOk());
    }

    @Test
    void retrieveProductList_returnsOk() throws Exception {
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_PRODUCTS_SUCCESS);
        when(productService.getListOfProduct(isNull(), anyInt(), anyInt(), anyString(), anyString(), isNull()))
            .thenReturn(response);

        mockMvc.perform(get("/api/v1/product/list"))
            .andExpect(status().isOk());
    }

    @Test
    void deleteProducts_returnsOk() throws Exception {
        List<Long> ids = List.of(1L, 2L);
        ResponseDTO response = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_LIST_OF_PRODUCTS_SUCCESS);

        when(productService.deleteProducts(anyList())).thenReturn(response);

        mockMvc.perform(post("/api/v1/product/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
            .andExpect(status().isOk());
    }

    // --- Helper methods ---

    private ProductDTO buildProductDTO() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Laptop");
        dto.setPrice(BigDecimal.valueOf(1500.00));
        dto.setIsActive(true);
        dto.setDescription("High-end laptop");
        return dto;
    }
}
