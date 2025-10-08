package com.tdbang.crm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.entities.JwkEntity;

@Repository
public interface JpaJwkRepository extends JpaRepository<JwkEntity, String> {
}