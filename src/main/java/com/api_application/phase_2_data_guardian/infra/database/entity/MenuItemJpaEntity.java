package com.api_application.phase_2_data_guardian.infra.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "menu_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_menu_items_restaurant_name",
                        columnNames = {"restaurant_id", "name"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemJpaEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "restaurant_id",
            nullable = false
    )
    private RestaurantJpaEntity restaurant;

    @Column(
            name = "name",
            nullable = false,
            length = 120
    )
    private String name;

    @Column(
            name = "description",
            nullable = false,
            length = 500
    )
    private String description;

    @Column(
            name = "price_in_cents",
            nullable = false
    )
    private Long priceInCents;

    @Column(
            name = "dine_in_only",
            nullable = false
    )
    private Boolean dineInOnly;

    @Column(
            name = "image_path",
            nullable = false,
            length = 500
    )
    private String imagePath;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private OffsetDateTime updatedAt;

    public MenuItemJpaEntity(
            RestaurantJpaEntity restaurant,
            String name,
            String description,
            Long priceInCents,
            Boolean dineInOnly,
            String imagePath
    ) {
        this.restaurant = restaurant;
        this.name = name;
        this.description = description;
        this.priceInCents = priceInCents;
        this.dineInOnly = dineInOnly;
        this.imagePath = imagePath;
    }

    @PrePersist
    private void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
