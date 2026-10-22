package com.api_application.phase_2_data_guardian.domain.entity;

import java.time.OffsetDateTime;

public class MenuItem {

    private Long id;
    private Long restaurantId;
    private String name;
    private String description;
    private Long priceInCents;
    private Boolean dineInOnly;
    private String imagePath;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public MenuItem() {
    }

    public MenuItem(
            Long restaurantId,
            String name,
            String description,
            Long priceInCents,
            Boolean dineInOnly,
            String imagePath
    ) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.priceInCents = priceInCents;
        this.dineInOnly = dineInOnly;
        this.imagePath = imagePath;
    }

    public MenuItem(
            Long id,
            Long restaurantId,
            String name,
            String description,
            Long priceInCents,
            Boolean dineInOnly,
            String imagePath,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.priceInCents = priceInCents;
        this.dineInOnly = dineInOnly;
        this.imagePath = imagePath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPriceInCents() {
        return priceInCents;
    }

    public void setPriceInCents(Long priceInCents) {
        this.priceInCents = priceInCents;
    }

    public Boolean getDineInOnly() {
        return dineInOnly;
    }

    public void setDineInOnly(Boolean dineInOnly) {
        this.dineInOnly = dineInOnly;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
