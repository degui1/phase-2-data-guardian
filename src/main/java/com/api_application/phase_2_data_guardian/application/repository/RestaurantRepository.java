package com.api_application.phase_2_data_guardian.application.repository;

import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {
    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById(Long id);

    List<Restaurant> findAll();

    void delete(Restaurant restaurant);
}
