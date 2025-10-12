package com.tdbang.crm.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tdbang.crm.entities.Product;
import com.tdbang.crm.mappers.ProductMapper;
import com.tdbang.crm.repositories.ProductRepository;
import com.tdbang.crm.repositories.custom.CustomRepository;
import com.tdbang.crm.specifications.SpecificationFilterUtil;
import com.tdbang.crm.specifications.builders.ProductSpecificationBuilder;
import com.tdbang.crm.specifications.builders.SpecificationBuilder;

@Log4j2
@Service
public class ProductService extends AbstractService<Product>{
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;

    public ProductService(SpecificationFilterUtil<Product> filterUtil, CustomRepository<Product> repository) {
        super(filterUtil, repository);
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
