package com.neosoft.app.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.neosoft.app.infrastructure.entity.Product;

@Transactional
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

}
