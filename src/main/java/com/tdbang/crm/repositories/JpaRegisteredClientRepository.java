package com.tdbang.crm.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.entities.RegisteredClientEntity;

@Repository
public interface JpaRegisteredClientRepository extends JpaRepository<RegisteredClientEntity, String> {
    Optional<RegisteredClientEntity> findByClientId(String clientId);
}
