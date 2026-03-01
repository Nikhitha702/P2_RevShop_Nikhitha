package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.CheckoutRequest;
import com.revshop.entity.*;
import com.revshop.repository.CartItemRepository;
import com.revshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    @Transactional
    public ApiResponse checkout(CheckoutRequest request) {
        User buyer = currentUserService.getCurrentUserOrThrow();
        List<CartItem> cartItems = cartItemRepository.findByBuyer(buyer);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Order order = new Order();
        order.setBuyer(buyer);
        order.setStatus(OrderStatus.PLACED);
        order.setShippingAddress(request.getShippingAddress().trim());
        order.setBillingAddress(request.getBillingAddress().trim());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for " + product.getName());
            }

            product.setQuantity(product.getQuantity() - cartItem.getQuantity());

            BigDecimal unitPrice = product.getDiscountedPrice() != null ? product.getDiscountedPrice() : product.getMrp();
            total = total.add(unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setSeller(product.getSeller());
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(unitPrice);
            orderItems.add(item);

            notificationService.createNotification(product.getSeller(), "New order received for product: " + product.getName());
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);
        orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);
        notificationService.createNotification(buyer, "Order placed successfully. Order ID: " + order.getId());

        return new ApiResponse(true, "Order placed successfully");
    }

    public List<Order> myOrders() {
        return orderRepository.findByBuyerOrderByCreatedAtDesc(currentUserService.getCurrentUserOrThrow());
    }

    public List<Order> sellerOrders() {
        return orderRepository.findByItemsSellerOrderByCreatedAtDesc(currentUserService.getCurrentUserOrThrow());
    }

    @Transactional
    public ApiResponse updateStatus(Long orderId, OrderStatus status) {
        User seller = currentUserService.getCurrentUserOrThrow();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        boolean sellerHasItem = order.getItems().stream()
                .anyMatch(i -> i.getSeller().getId().equals(seller.getId()));
        if (!sellerHasItem) {
            throw new IllegalArgumentException("You can update only your own order items");
        }

        order.setStatus(status);
        orderRepository.save(order);
        notificationService.createNotification(order.getBuyer(), "Order " + order.getId() + " updated to " + status);
        return new ApiResponse(true, "Order status updated");
    }
}
