package com.api_application.phase_2_data_guardian.application.repository;

import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MenuItemRepository {

    MenuItem save(MenuItem menuItem);

    Optional<MenuItem> findById(Long id);

    Page<MenuItem> findAll(Pageable pageable);

    void delete(MenuItem menuItem);

    Page<MenuItem> findByRestaurantId(Long restaurantId, Pageable pageable);
}
