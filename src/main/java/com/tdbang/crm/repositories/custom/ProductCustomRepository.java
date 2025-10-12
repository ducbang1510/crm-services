package com.tdbang.crm.repositories.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.entities.Product;

@Repository
public class ProductCustomRepository extends CustomRepository<Product> {

    protected ProductCustomRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Root<Product> getTupleRoot(CriteriaQuery<Tuple> query) {
        return query.from(Product.class);
    }

    @Override
    protected Root<Product> getLongRoot(CriteriaQuery<Long> query) {
        return query.from(Product.class);
    }

    @Override
    protected Class<Product> getEntityClass() {
        return Product.class;
    }
}
