package com.api_application.phase_2_data_guardian.infra.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "restaurants")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantJpaEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Embedded
    private AddressJpaEmbeddable address;

    @Column(name = "cuisine_type", nullable = false, length = 80)
    private String cuisineType;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "owner_id",
            nullable = false
    )
    private UserJpaEntity owner;

    @OneToMany(
            mappedBy = "restaurant",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("dayOfWeek ASC, opensAt ASC")
    private List<RestaurantOpeningPeriodJpaEntity> openingPeriods =
            new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    public RestaurantJpaEntity(
            String name,
            AddressJpaEmbeddable address,
            String cuisineType,
            UserJpaEntity owner
    ) {
        this.name = name;
        this.address = address;
        this.cuisineType = cuisineType;
        this.owner = owner;
    }

    public void addOpeningPeriod(
            RestaurantOpeningPeriodJpaEntity openingPeriod
    ) {
        openingPeriods.add(openingPeriod);
        openingPeriod.setRestaurant(this);
    }

    public void removeOpeningPeriod(
            RestaurantOpeningPeriodJpaEntity openingPeriod
    ) {
        openingPeriods.remove(openingPeriod);
        openingPeriod.setRestaurant(null);
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
