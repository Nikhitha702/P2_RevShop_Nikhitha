package com.revshop.repository;

import com.revshop.entity.Order;
import com.revshop.entity.OrderStatus;
import com.revshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    List<Order> findByUserId(Long userId);

    boolean existsByUserIdAndStatusAndItemsProductId(Long userId, OrderStatus status, Long productId);

    @Query("""
            select distinct o from Order o
            join o.items i
            where i.product.seller.id = :sellerId
            order by o.createdAt desc
            """)
    List<Order> findOrdersForSeller(@Param("sellerId") Long sellerId);
}
