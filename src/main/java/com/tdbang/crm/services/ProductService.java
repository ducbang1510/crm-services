/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tdbang.crm.dtos.ProductDTO;
import com.tdbang.crm.dtos.ResponseDTO;
import com.tdbang.crm.entities.Product;
import com.tdbang.crm.exceptions.CRMException;
import com.tdbang.crm.mappers.ProductMapper;
import com.tdbang.crm.repositories.ProductRepository;
import com.tdbang.crm.repositories.custom.CustomRepository;
import com.tdbang.crm.specifications.SpecificationFilterUtil;
import com.tdbang.crm.specifications.builders.ProductSpecificationBuilder;
import com.tdbang.crm.specifications.builders.SpecificationBuilder;
import com.tdbang.crm.utils.AppConstants;
import com.tdbang.crm.utils.AppUtils;
import com.tdbang.crm.utils.MessageConstants;

@Log4j2
@Service
public class ProductService extends AbstractService<Product> {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;

    public ProductService(SpecificationFilterUtil<Product> filterUtil, CustomRepository<Product> repository) {
        super(filterUtil, repository);
    }

    public ResponseDTO getListOfProduct(String filter, int pageSize, int pageNumber, String sortColumn, String sortOrder,
                                        String fields) {
        ResponseDTO result;
        try {
            List<ProductDTO> productDTOList = new ArrayList<>();

            Map<String, Object> resultMapQuery = get(filter, pageSize, pageNumber, sortColumn, sortOrder, AppUtils.convertFields(fields));
            List<Product> results = productMapper.mapRecordList(resultMapQuery);
            for (Product r : results) {
                productDTOList.add(productMapper.mappingProductEntityToProductDTO(r));
            }

            resultMapQuery.replace(AppConstants.RECORD_LIST_KEY, productDTOList);
            if (pageSize == 0) {
                result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_PRODUCTS_SUCCESS, productDTOList);
            } else {
                result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_LIST_OF_PRODUCTS_SUCCESS, resultMapQuery);
            }
        } catch (Exception e) {
            throw new CRMException(HttpStatus.INTERNAL_SERVER_ERROR,
                MessageConstants.INTERNAL_ERROR_CODE, MessageConstants.INTERNAL_ERROR_MESSAGE, e.getMessage());
        }

        return result;
    }

    @Transactional
    public ResponseDTO createNewProduct(ProductDTO productDTO) {
        ResponseDTO result;
        try {
            Product saveProduct = new Product();
            productMapper.mappingProductDTOToEntity(productDTO, saveProduct, true);
            productRepository.save(saveProduct);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.CREATING_NEW_PRODUCT_SUCCESS);
        } catch (Exception e) {
            throw new CRMException(HttpStatus.BAD_REQUEST, MessageConstants.BAD_REQUEST_CODE, MessageConstants.CREATING_NEW_PRODUCT_ERROR, e.getMessage());
        }
        return result;
    }

    public ResponseDTO getProductDetails(Long productPk) {
        ResponseDTO result = new ResponseDTO();
        if (productPk != null) {
            Product product = productRepository.findByPk(productPk)
                .orElseThrow(() -> new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, MessageConstants.NOT_FOUND_MESSAGE));
            ProductDTO productDTO = productMapper.mappingProductEntityToProductDTO(product);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.FETCHING_CONTACT_SUCCESS, productDTO);
        }

        return result;
    }

    @Transactional
    public ResponseDTO updateProductDetails(Long productPk, Long creatorFk, ProductDTO productDTO) {
        ResponseDTO result;
        Product updatedProduct = productRepository.findByPk(productPk).orElse(null);
        if (updatedProduct != null) {
            productMapper.mappingProductDTOToEntity(productDTO, updatedProduct, false);
            productRepository.save(updatedProduct);
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.UPDATING_PRODUCT_SUCCESS);
        } else {
            throw new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, MessageConstants.NOT_FOUND_MESSAGE);
        }
        return result;
    }

    public ResponseDTO deleteProducts(List<Long> productPks) {
        ResponseDTO result;
        List<Product> deletedListProducts = productRepository.getProductsByProductPks(productPks);
        if (productPks.size() == deletedListProducts.size()) {
            productRepository.deleteAllById(deletedListProducts.stream().map(Product::getPk).toList());
            result = new ResponseDTO(MessageConstants.SUCCESS_STATUS, MessageConstants.DELETING_LIST_OF_PRODUCTS_SUCCESS);
        } else {
            throw new CRMException(HttpStatus.NOT_FOUND, MessageConstants.NOT_FOUND_CODE, "One or more products can not be found");
        }
        return result;
    }

    @Override
    protected String getProfileFields() {
        return "pk,name,price,isActive,description,createdOn,updatedOn";
    }

    @Override
    protected String getDefaultSortColumn() {
        return "name";
    }

    @Override
    protected Class<Product> getEntityClass() {
        return Product.class;
    }

    @Override
    protected SpecificationBuilder<Product> getSpecificationBuilder() {
        return new ProductSpecificationBuilder();
    }
}
