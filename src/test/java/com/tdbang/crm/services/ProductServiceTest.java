/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import com.tdbang.crm.dtos.ProductDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.entities.Product;
import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.mappers.ProductMapper;
import com.tdbang.crm.repositories.ProductRepository;
import com.tdbang.crm.repositories.custom.CustomRepository;
import com.tdbang.crm.specifications.SpecificationFilterUtil;
import com.tdbang.crm.utils.MessageConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private SpecificationFilterUtil<Product> filterUtil;

    @Mock
    private CustomRepository<Product> customRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(filterUtil, customRepository);
        ReflectionTestUtils.setField(productService, "productRepository", productRepository);
        ReflectionTestUtils.setField(productService, "productMapper", productMapper);
    }

    @Test
    void createNewProduct_withValidData_returnsSuccess() {
        ProductDTO productDTO = buildProductDTO();
        Product savedProduct = new Product();
        savedProduct.setPk(1L);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ResponseDTO result = productService.createNewProduct(productDTO);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.CREATING_NEW_PRODUCT_SUCCESS, result.getMsg());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createNewProduct_withRepositoryException_throwsCRMException() {
        ProductDTO productDTO = buildProductDTO();

        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("DB error"));

        CRMException ex = assertThrows(CRMException.class,
            () -> productService.createNewProduct(productDTO));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void getProductDetails_withValidId_returnsSuccess() {
        Long productPk = 1L;
        Product product = buildProduct(productPk);
        ProductDTO productDTO = buildProductDTO();
        productDTO.setPk(productPk);

        when(productRepository.findByPk(productPk)).thenReturn(Optional.of(product));
        when(productMapper.mappingProductEntityToProductDTO(product)).thenReturn(productDTO);

        ResponseDTO result = productService.getProductDetails(productPk);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
    }

    @Test
    void getProductDetails_withNullId_returnsEmptyResponseWithoutCallingRepo() {
        ResponseDTO result = productService.getProductDetails(null);

        assertNotNull(result);
        verify(productRepository, times(0)).findByPk(anyLong());
    }

    @Test
    void getProductDetails_withNotFoundId_throwsNotFound() {
        when(productRepository.findByPk(anyLong())).thenReturn(Optional.empty());

        CRMException ex = assertThrows(CRMException.class,
            () -> productService.getProductDetails(999L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void updateProductDetails_withExistingProduct_returnsSuccess() {
        Long productPk = 1L;
        ProductDTO productDTO = buildProductDTO();
        Product existing = buildProduct(productPk);

        when(productRepository.findByPk(productPk)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenReturn(existing);

        ResponseDTO result = productService.updateProductDetails(productPk, 1L, productDTO);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.UPDATING_PRODUCT_SUCCESS, result.getMsg());
    }

    @Test
    void updateProductDetails_withNotFoundProduct_throwsNotFound() {
        when(productRepository.findByPk(anyLong())).thenReturn(Optional.empty());

        CRMException ex = assertThrows(CRMException.class,
            () -> productService.updateProductDetails(999L, 1L, buildProductDTO()));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void deleteProducts_withAllFoundProducts_returnsSuccess() {
        List<Long> ids = List.of(1L, 2L);
        Product p1 = buildProduct(1L);
        Product p2 = buildProduct(2L);

        when(productRepository.getProductsByProductPks(ids)).thenReturn(List.of(p1, p2));

        ResponseDTO result = productService.deleteProducts(ids);

        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.DELETING_LIST_OF_PRODUCTS_SUCCESS, result.getMsg());
        verify(productRepository, times(1)).deleteAllById(any());
    }

    @Test
    void deleteProducts_withMissingProducts_throwsNotFound() {
        List<Long> ids = List.of(1L, 2L, 3L);
        Product p1 = buildProduct(1L);

        when(productRepository.getProductsByProductPks(ids)).thenReturn(List.of(p1));

        CRMException ex = assertThrows(CRMException.class,
            () -> productService.deleteProducts(ids));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void getListOfProduct_withNoPageSize_returnsAllProducts() {
        Product product = buildProduct(1L);
        ProductDTO productDTO = buildProductDTO();

        when(customRepository.findAll(any(Sort.class), any())).thenReturn(List.of());
        when(productMapper.mapRecordList(any())).thenReturn(List.of(product));
        when(productMapper.mappingProductEntityToProductDTO(product)).thenReturn(productDTO);

        ResponseDTO result = productService.getListOfProduct(null, 0, 0, "pk", "ASC", null);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        assertEquals(MessageConstants.FETCHING_LIST_OF_PRODUCTS_SUCCESS, result.getMsg());
    }

    @Test
    void getListOfProduct_withPageSize_returnsPagedProducts() {
        Page<Map<String, Object>> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(customRepository.findAll(any(Pageable.class), any())).thenReturn(page);
        when(productMapper.mapRecordList(any())).thenReturn(List.of());

        ResponseDTO result = productService.getListOfProduct(null, 10, 0, "pk", "ASC", null);

        assertNotNull(result);
        assertEquals(MessageConstants.SUCCESS_STATUS, result.getStatus());
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertNotNull(data.get("totalRecord"));
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

    private Product buildProduct(Long pk) {
        Product product = new Product();
        product.setPk(pk);
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(1500.00));
        product.setIsActive(true);
        return product;
    }
}
