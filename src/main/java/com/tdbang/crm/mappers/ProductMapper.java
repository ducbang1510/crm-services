/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.mappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tdbang.crm.dtos.ProductDTO;
import com.tdbang.crm.entities.Product;
import com.tdbang.crm.utils.AppConstants;

@Component
public class ProductMapper {

    @Autowired
    private ModelMapper modelMapper;

    public List<Product> mapRecordList(Map<String, Object> resultMap) {
        List<Product> products = new ArrayList<>();

        // Extract the "recordList" value
        Object recordsObj = resultMap.get(AppConstants.RECORD_LIST_KEY);
        if (recordsObj instanceof List<?>) {
            List<?> recordList = (List<?>) recordsObj;

            for (Object obj : recordList) {
                if (obj instanceof Map) {
                    Map<String, Object> productMap = (Map<String, Object>) obj;

                    // Map to Product
                    Product product = modelMapper.map(productMap, Product.class);
                    products.add(product);
                }
            }
        }

        return products;
    }

    public Product mappingProductDTOToEntity(ProductDTO productDTO, boolean isCreateNew) {
        Product product = new Product();
        product.setPk(productDTO.getPk());
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setIsActive(productDTO.getIsActive());
        product.setDescription(productDTO.getDescription());
        product.setName(productDTO.getName());
        if (isCreateNew) {
            product.setUpdatedOn(new Date());
        }
        return product;
    }

    public ProductDTO mappingProductEntityToProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setPk(product.getPk());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setIsActive(product.getIsActive());
        productDTO.setDescription(product.getDescription());
        productDTO.setCreatedTime(product.getCreatedOn());
        productDTO.setUpdatedTime(product.getUpdatedOn());
        return productDTO;
    }
}
