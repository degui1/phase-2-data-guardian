package com.api_application.phase_2_data_guardian.domain.entity;

import java.time.OffsetDateTime;

public class User {

    private Long id;
    private String name;
    private String email;
    private String password;
    private Long userTypeId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public User() {
    }

    public User(String name, String email, String password, Long userTypeId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userTypeId = userTypeId;
    }

    public User(
            Long id,
            String name,
            String email,
            String password,
            Long userTypeId,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userTypeId = userTypeId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Long getUserTypeId() { return userTypeId; }
    public void setUserTypeId(Long userTypeId) { this.userTypeId = userTypeId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
