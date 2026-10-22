package com.api_application.phase_2_data_guardian.infra.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserJpaEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "name",
            nullable = false,
            length = 120
    )
    private String name;

    @Column(
            name = "email",
            nullable = false,
            unique = true,
            length = 150
    )
    private String email;

    @Column(
            name = "password_hash",
            nullable = false,
            length = 255
    )
    private String passwordHash;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_type_id",
            nullable = false
    )
    private UserTypeJpaEntity userType;

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

    public UserJpaEntity(
            String name,
            String email,
            String passwordHash,
            UserTypeJpaEntity userType
    ) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.userType = userType;
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
