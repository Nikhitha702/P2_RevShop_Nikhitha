package com.revshop.repository;

import com.revshop.entity.Product;
import com.revshop.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByCategoryNameIgnoreCase(String categoryName, Pageable pageable);
    List<Product> findBySeller(Seller seller);
    List<Product> findBySellerAndQuantityLessThanEqual(Seller seller, Integer quantity);
}
