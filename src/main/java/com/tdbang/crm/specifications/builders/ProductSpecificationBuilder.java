/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.specifications.builders;

import org.springframework.data.jpa.domain.Specification;

import com.tdbang.crm.entities.Product;
import com.tdbang.crm.specifications.ProductSpecification;

public class ProductSpecificationBuilder extends SpecificationBuilder<Product> {
    @Override
    public Specification<Product> build() {
        if (getParams().isEmpty()) {
            return null;
        }
        Specification<Product> result = new ProductSpecification(getParams().get(0));
        for (int i = 1; i < getParams().size(); i++) {
            result = getParams().get(i).isOrPredicate()
                    ? Specification.anyOf(result, new ProductSpecification(getParams().get(i)))
                    : Specification.allOf(result, new ProductSpecification(getParams().get(i)));
        }
        return result;
    }
}
