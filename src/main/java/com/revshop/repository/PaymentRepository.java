package com.revshop.repository;

import com.revshop.entity.Payment;
import com.revshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Order order);
    List<Payment> findByOrderIn(List<Order> orders);
}
