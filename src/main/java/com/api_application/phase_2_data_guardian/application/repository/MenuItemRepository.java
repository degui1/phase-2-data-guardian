package com.api_application.phase_2_data_guardian.application.repository;

import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository {

    MenuItem save(MenuItem menuItem);

    Optional<MenuItem> findById(Long id);

    List<MenuItem> findAll();

    void delete(MenuItem menuItem);

    List<MenuItem> findByRestaurantId(Long restaurantId);
}
