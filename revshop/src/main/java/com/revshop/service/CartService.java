package com.revshop.service;

import com.revshop.dto.ApiResponse;
import com.revshop.entity.Cart;
import com.revshop.entity.CartItem;
import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.repository.CartItemRepository;
import com.revshop.repository.CartRepository;
import com.revshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;

    public ApiResponse addToCart(Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        User user = currentUserService.getCurrentUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        int requestedQuantity = quantity;
        if (existingItem.isPresent()) {
            requestedQuantity = existingItem.get().getQuantity() + quantity;
        }
        if (requestedQuantity > product.getQuantity()) {
            throw new RuntimeException("Requested quantity exceeds available stock");
        }

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(requestedQuantity);
            item.setSubtotal(resolveUnitPrice(product).multiply(BigDecimal.valueOf(requestedQuantity)));
            cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .subtotal(resolveUnitPrice(product).multiply(BigDecimal.valueOf(quantity)))
                    .build();
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return new ApiResponse(true, "Product added to cart");
    }

    public Cart viewCart() {
        User user = currentUserService.getCurrentUser();
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));
    }

    public ApiResponse removeFromCart(Long cartItemId) {
        CartItem item = getOwnedCartItem(cartItemId);
        cartItemRepository.delete(item);
        return new ApiResponse(true, "Item removed from cart");
    }

    public ApiResponse updateQuantity(Long cartItemId, Integer quantity) {
        CartItem item = getOwnedCartItem(cartItemId);

        if (quantity == null || quantity <= 0) {
            cartItemRepository.delete(item);
            return new ApiResponse(true, "Item removed from cart");
        }

        if (quantity > item.getProduct().getQuantity()) {
            throw new RuntimeException("Requested quantity exceeds available stock");
        }

        item.setQuantity(quantity);
        item.setSubtotal(resolveUnitPrice(item.getProduct()).multiply(BigDecimal.valueOf(quantity)));
        cartItemRepository.save(item);
        return new ApiResponse(true, "Cart updated successfully");
    }

    public BigDecimal calculateTotal() {
        Cart cart = viewCart();
        return cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public ApiResponse clearCart() {
        Cart cart = viewCart();
        cartItemRepository.deleteAll(cart.getItems());
        return new ApiResponse(true, "Cart cleared successfully");
    }

    private CartItem getOwnedCartItem(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        User user = currentUserService.getCurrentUser();
        if (!item.getCart().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized cart access");
        }
        return item;
    }

    private BigDecimal resolveUnitPrice(Product product) {
        return product.getDiscountedPrice() == null ? product.getPrice() : product.getDiscountedPrice();
    }
}
