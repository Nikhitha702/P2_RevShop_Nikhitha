package com.revshop.repository;

import com.revshop.entity.Product;
import com.revshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByActiveTrue(Pageable pageable);
    Page<Product> findByActiveTrueAndNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Product> findByActiveTrueAndCategoryNameIgnoreCase(String categoryName, Pageable pageable);
    List<Product> findBySeller(User seller);
}
