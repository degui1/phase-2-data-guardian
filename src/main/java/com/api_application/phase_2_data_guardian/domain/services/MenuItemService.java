package com.api_application.phase_2_data_guardian.domain.services;

import com.api_application.phase_2_data_guardian.application.repository.MenuItemRepository;
import com.api_application.phase_2_data_guardian.application.repository.RestaurantRepository;
import com.api_application.phase_2_data_guardian.application.usecase.MenuItemUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;

import java.nio.file.AccessDeniedException;
import java.util.List;

public class MenuItemService implements MenuItemUseCase {

    private final MenuItemRepository repository;
    private final RestaurantRepository restaurantRepository;

    public MenuItemService(
            MenuItemRepository repository,
            RestaurantRepository restaurantRepository
    ) {
        this.repository = repository;
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public MenuItem create(Long userId, MenuItem menuItem) throws AccessDeniedException {
        checkOwner(userId, menuItem.getRestaurantId());

        return repository.save(menuItem);
    }

    @Override
    public List<MenuItem> findByRestaurantId(Long restaurantId) {
        return repository.findByRestaurantId(restaurantId);
    }

    @Override
    public MenuItem findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Menu not found"));
    }

    @Override
    public MenuItem update(Long userId, MenuItem menuItem) throws AccessDeniedException {
        MenuItem menu = repository
                .findById(menuItem.getId())
                .orElseThrow(() -> new IllegalArgumentException("Menu item does not exist"));


        checkOwner(userId, menu.getRestaurantId());

        String newName = menuItem.getName();
        Long price = menuItem.getPriceInCents();

        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Name invalid");
        }

        if (price == null || price < 0) {
            throw new IllegalArgumentException("Price must be grater or equal to zero");
        }

        menu.setName(menuItem.getName());
        menu.setDescription(menuItem.getDescription());
        menu.setPriceInCents(menuItem.getPriceInCents());
        menu.setDineInOnly(menuItem.getDineInOnly());
        menu.setImagePath(menuItem.getImagePath());

        return repository.save(menu);
    }

    @Override
    public void delete(Long userId, Long id) throws AccessDeniedException, IllegalArgumentException {
        MenuItem menu = repository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item does not exist"));

        checkOwner(userId, menu.getRestaurantId());

        repository.delete(menu);
    }

    private void checkOwner(Long userId, Long restaurantId) throws AccessDeniedException {
        Restaurant rest = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        if (!rest.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("Only the restaurant can its own menu");
        }
    }
}
