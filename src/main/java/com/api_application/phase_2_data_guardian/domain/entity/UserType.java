package com.api_application.phase_2_data_guardian.domain.entity;

import java.time.OffsetDateTime;

public class UserType {

    private Long id;
    private String name;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UserType() {
    }

    public UserType(String name) {
        this.name = name;
    }

    public UserType(Long id, String name, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
