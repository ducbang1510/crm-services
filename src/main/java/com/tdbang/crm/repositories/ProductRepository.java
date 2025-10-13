package com.tdbang.crm.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tdbang.crm.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByPk(Long pk);

    @Query(value = "SELECT p FROM Product p WHERE p.pk IN (:pks)")
    List<Product> getProductsByProductPks(List<Long> pks);
}
