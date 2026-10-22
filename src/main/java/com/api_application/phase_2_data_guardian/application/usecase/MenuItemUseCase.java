package com.api_application.phase_2_data_guardian.application.usecase;

import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface MenuItemUseCase {

    MenuItem create(Long userId, MenuItem item) throws AccessDeniedException, IllegalArgumentException;

    List<MenuItem> findByRestaurantId(Long restaurantId);

    MenuItem findById(Long id);

    MenuItem update(Long userId, MenuItem data) throws AccessDeniedException;

    void delete(Long userId, Long id) throws AccessDeniedException;
}