package com.revshop.repository;

import com.revshop.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class RepositoryLayerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void userRepositoryShouldFindByEmailIgnoreCase() {
        User buyer = createBuyer("buyer@test.com");
        userRepository.save(buyer);

        assertTrue(userRepository.findByEmailIgnoreCase("BUYER@TEST.COM").isPresent());
    }

    @Test
    void productRepositoryShouldBrowseAndSearchActiveProducts() {
        User seller = userRepository.save(createSeller("seller@test.com"));
        Category category = createCategory("Electronics");

        Product product = createProduct("Phone Ultra", category, seller);
        productRepository.save(product);

        assertEquals(1, productRepository.findByActiveTrue(PageRequest.of(0, 10)).getTotalElements());
        assertEquals(1,
                productRepository.findByActiveTrueAndNameContainingIgnoreCase("ultra", PageRequest.of(0, 10))
                        .getTotalElements());
    }

    @Test
    void favoriteAndReviewRepositoriesShouldReturnBuyerData() {
        User buyer = userRepository.save(createBuyer("favbuyer@test.com"));
        User seller = userRepository.save(createSeller("favseller@test.com"));
        Category category = createCategory("Mobiles");
        Product product = productRepository.save(createProduct("Phone", category, seller));

        Favorite favorite = new Favorite();
        favorite.setBuyer(buyer);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);

        ProductReview review = new ProductReview();
        review.setBuyer(buyer);
        review.setProduct(product);
        review.setRating(4);
        review.setReviewText("Good product");
        productReviewRepository.save(review);

        assertTrue(favoriteRepository.findByBuyerAndProductId(buyer, product.getId()).isPresent());
        assertEquals(1, productReviewRepository.findByProductIdOrderByCreatedAtDesc(product.getId()).size());
        assertEquals(4.0, productReviewRepository.averageRatingForProduct(product.getId()));
    }

    @Test
    void orderRepositoryShouldFindOrdersByBuyerAndProduct() {
        User buyer = userRepository.save(createBuyer("orderbuyer@test.com"));
        User seller = userRepository.save(createSeller("orderseller@test.com"));
        Category category = createCategory("Accessories");
        Product product = productRepository.save(createProduct("Headset", category, seller));

        Order order = new Order();
        order.setBuyer(buyer);
        order.setStatus(OrderStatus.PLACED);
        order.setShippingAddress("Hyd");
        order.setBillingAddress("Hyd");
        order.setTotalAmount(BigDecimal.valueOf(1500));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setSeller(seller);
        orderItem.setQuantity(1);
        orderItem.setUnitPrice(BigDecimal.valueOf(1500));
        order.setItems(List.of(orderItem));

        orderRepository.save(order);

        assertTrue(orderRepository.existsByBuyerAndItemsProductId(buyer, product.getId()));
        assertEquals(1, orderRepository.findByBuyerOrderByCreatedAtDesc(buyer).size());
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(name + " category");
        category.setActive(true);
        return categoryRepository.save(category);
    }

    private Product createProduct(String name, Category category, User seller) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(name + " description");
        product.setMrp(BigDecimal.valueOf(20000));
        product.setDiscountedPrice(BigDecimal.valueOf(18000));
        product.setQuantity(5);
        product.setInventoryThreshold(1);
        product.setActive(true);
        product.setCategory(category);
        product.setSeller(seller);
        return product;
    }

    private User createBuyer(String email) {
        User user = new User();
        user.setFirstName("Buyer");
        user.setLastName("One");
        user.setEmail(email);
        user.setPassword("enc-pass");
        user.setPhone("9999999999");
        user.setAddress("Address");
        user.setRole(Role.ROLE_BUYER);
        user.setEnabled(true);
        return user;
    }

    private User createSeller(String email) {
        User user = new User();
        user.setFirstName("Seller");
        user.setLastName("One");
        user.setEmail(email);
        user.setPassword("enc-pass");
        user.setPhone("8888888888");
        user.setAddress("Address");
        user.setRole(Role.ROLE_SELLER);
        user.setBusinessName("Shop");
        user.setGstNumber("GST123");
        user.setBusinessCategory("Electronics");
        user.setEnabled(true);
        return user;
    }
}
