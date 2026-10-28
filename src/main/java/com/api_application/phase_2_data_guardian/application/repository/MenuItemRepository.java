package com.api_application.phase_2_data_guardian.application.repository;

import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MenuItemRepository {

    MenuItem save(MenuItem menuItem);

    Optional<MenuItem> findByIdAndRestaurantId(Long id, Long restaurantId);

    boolean existsByRestaurantId(Long restaurantId);

    void delete(MenuItem menuItem);

    Page<MenuItem> findByRestaurantId(Long restaurantId, Pageable pageable);
}
