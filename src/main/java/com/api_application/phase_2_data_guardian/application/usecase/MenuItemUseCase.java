package com.api_application.phase_2_data_guardian.application.usecase;

import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MenuItemUseCase {
    MenuItem create(Long userId, MenuItem item);
    Page<MenuItem> findByRestaurantId(Long restaurantId, Pageable pageable);
    MenuItem findById(Long restaurantId, Long id);
    MenuItem update(Long userId, Long restaurantId, Long id, MenuItem data);
    void delete(Long userId, Long restaurantId, Long id);
}
