package com.tdbang.crm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
