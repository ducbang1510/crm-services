/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.repositories.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.entities.User;

@Repository
public class UserCustomRepository extends CustomRepository<User> {
    protected UserCustomRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Root<User> getTupleRoot(CriteriaQuery<Tuple> query) {
        return query.from(User.class);
    }

    @Override
    protected Root<User> getLongRoot(CriteriaQuery<Long> query) {
        return query.from(User.class);
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }
}
