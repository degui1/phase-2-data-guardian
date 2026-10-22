package com.api_application.phase_2_data_guardian.application.usecase;

import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;

import java.util.List;

public interface MenuItemUseCase {

    MenuItem create(Long userId, MenuItem item);

    List<MenuItem> findByRestaurantId(Long restaurantId);

    MenuItem findById(Long id);

    MenuItem update(Long userId, MenuItem data);

    void delete(Long userId, Long id);
}
