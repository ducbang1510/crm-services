/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.repositories.authorization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.entities.authorization.JwkEntity;

@Repository
public interface JpaJwkRepository extends JpaRepository<JwkEntity, String> {
}