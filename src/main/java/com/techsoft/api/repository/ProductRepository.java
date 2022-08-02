package com.techsoft.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techsoft.api.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
