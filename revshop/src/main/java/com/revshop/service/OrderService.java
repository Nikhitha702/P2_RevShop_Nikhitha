package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.dto.CheckoutRequest;
import com.revshop.entity.Cart;
import com.revshop.entity.CartItem;
import com.revshop.entity.Order;
import com.revshop.entity.OrderItem;
import com.revshop.entity.OrderStatus;
import com.revshop.entity.Product;
import com.revshop.entity.Seller;
import com.revshop.entity.User;
import com.revshop.repository.CartItemRepository;
import com.revshop.repository.CartRepository;
import com.revshop.repository.OrderItemRepository;
import com.revshop.repository.OrderRepository;
import com.revshop.repository.ProductRepository;
import com.revshop.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;
    private final ProductService productService;

    @Transactional
    public ApiResponse checkout(CheckoutRequest request) {
        User user = currentUserService.getCurrentUser();
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart is empty"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = Order.builder()
                .user(user)
                .shippingAddress(resolveAddress(request == null ? null : request.getShippingAddress(), user.getAddress()))
                .billingAddress(resolveAddress(request == null ? null : request.getBillingAddress(), user.getAddress()))
                .status(OrderStatus.PLACED)
                .createdAt(LocalDateTime.now())
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (cartItem.getQuantity() > product.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            BigDecimal price = product.getDiscountedPrice() == null ? product.getPrice() : product.getDiscountedPrice();
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(price)
                    .build();

            order.getItems().add(orderItem);
            total = total.add(lineTotal);

            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
            productService.notifySellerOnLowStock(product);

            notificationService.createNotification(product.getSeller().getUser(),
                    "New order received for product: " + product.getName());
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(savedOrder.getItems());

        cartItemRepository.deleteAll(cart.getItems());

        notificationService.createNotification(user, "Order placed successfully. Order ID: " + savedOrder.getId());
        return new ApiResponse(true, "Order placed successfully");
    }

    public List<Order> getMyOrders() {
        User user = currentUserService.getCurrentUser();
        return orderRepository.findByUser(user);
    }

    public List<Order> getSellerOrders() {
        User user = currentUserService.getCurrentUser();
        Seller seller = sellerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));
        return orderRepository.findOrdersForSeller(seller.getId());
    }

    public ApiResponse updateOrderStatus(Long orderId, OrderStatus status) {
        User user = currentUserService.getCurrentUser();
        Seller seller = sellerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        boolean sellerOwnsAnyItem = order.getItems().stream()
                .anyMatch(item -> item.getProduct().getSeller().getId().equals(seller.getId()));
        if (!sellerOwnsAnyItem) {
            throw new RuntimeException("Unauthorized order access");
        }

        order.setStatus(status);
        orderRepository.save(order);
        notificationService.createNotification(order.getUser(), "Your order " + order.getId() + " is now " + status);
        return new ApiResponse(true, "Order status updated successfully");
    }

    private String resolveAddress(String providedAddress, String fallbackAddress) {
        String candidate = providedAddress == null || providedAddress.isBlank() ? fallbackAddress : providedAddress;
        if (candidate == null || candidate.isBlank()) {
            throw new RuntimeException("Shipping/Billing address is required");
        }
        return candidate;
    }
}
