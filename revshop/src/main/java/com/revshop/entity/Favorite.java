package com.revshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "favorites",
        uniqueConstraints = @UniqueConstraint(name = "uk_favorite_buyer_product", columnNames = {"buyer_id", "product_id"})
)
@Getter
@Setter
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "buyer_id")
    @JsonIgnoreProperties({"password", "address", "phone", "businessName", "gstNumber", "businessCategory", "enabled"})
    private User buyer;

    // Legacy compatibility: some DBs still have non-null user_id in favorites.
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"seller"})
    private Product product;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    private void syncLegacyUserId() {
        if (buyer != null && buyer.getId() != null) {
            this.userId = buyer.getId();
        }
    }
}
