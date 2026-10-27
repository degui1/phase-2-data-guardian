package com.api_application.phase_2_data_guardian.application.usecase;

import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantUseCase {
    Restaurant create(Restaurant restaurant);
    Page<Restaurant> findAll(Pageable pageable);
    Restaurant findById(Long id);
    Restaurant update(Long authenticatedUserId, Long id, Restaurant restaurant);
    void delete(Long authenticatedUserId, Long id);
}
