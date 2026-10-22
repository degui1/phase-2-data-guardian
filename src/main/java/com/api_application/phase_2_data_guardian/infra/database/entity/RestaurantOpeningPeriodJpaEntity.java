package com.api_application.phase_2_data_guardian.infra.database.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(
        name = "restaurant_opening_periods",
        uniqueConstraints = {
                @UniqueConstraint(
                        name="uk_restaurant_opening_period",
                        columnNames = {"restaurant_id", "day_of_week", "opens_at", "closes_at"}
                )
        }
)
@NoArgsConstructor
@Getter
@Setter
public class RestaurantOpeningPeriodJpaEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantJpaEntity restaurant;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private DayOfWeek dayOfWeek;

    @Column(name = "opens_at", nullable = false)
    private LocalTime opensAt;

    @Column(name = "closes_at", nullable = false)
    private LocalTime closesAt;

    public RestaurantOpeningPeriodJpaEntity(
            DayOfWeek dayOfWeek,
            LocalTime opensAt,
            LocalTime closesAt
    ) {
        this.dayOfWeek = dayOfWeek;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
    }
}
