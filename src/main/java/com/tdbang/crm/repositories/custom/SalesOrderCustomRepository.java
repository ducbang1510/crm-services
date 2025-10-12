package com.tdbang.crm.repositories.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.entities.SalesOrder;

@Repository
public class SalesOrderCustomRepository extends CustomRepository<SalesOrder> {
    protected SalesOrderCustomRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Root<SalesOrder> getTupleRoot(CriteriaQuery<Tuple> query) {
        return query.from(SalesOrder.class);
    }

    @Override
    protected Root<SalesOrder> getLongRoot(CriteriaQuery<Long> query) {
        return query.from(SalesOrder.class);
    }

    @Override
    protected Class<SalesOrder> getEntityClass() {
        return SalesOrder.class;
    }
}
