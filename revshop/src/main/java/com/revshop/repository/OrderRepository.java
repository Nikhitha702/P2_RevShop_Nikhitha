package com.revshop.repository;

import com.revshop.entity.Order;
import com.revshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerOrderByCreatedAtDesc(User buyer);
    List<Order> findByItemsSellerOrderByCreatedAtDesc(User seller);
}
