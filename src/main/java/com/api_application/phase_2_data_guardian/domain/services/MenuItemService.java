package com.api_application.phase_2_data_guardian.domain.services;

import com.api_application.phase_2_data_guardian.application.repository.MenuItemRepository;
import com.api_application.phase_2_data_guardian.application.repository.RestaurantRepository;
import com.api_application.phase_2_data_guardian.application.usecase.MenuItemUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.domain.exception.BusinessRuleException;
import com.api_application.phase_2_data_guardian.domain.exception.ForbiddenOperationException;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    public MenuItem create(Long userId, MenuItem menuItem) {
        checkOwner(userId, menuItem.getRestaurantId());

        return repository.save(menuItem);
    }

    @Override
    public Page<MenuItem> findByRestaurantId(Long restaurantId, Pageable pageable) {
        restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Restaurant",
                                restaurantId
                        )
                );

        return repository.findByRestaurantId(restaurantId, pageable);
    }

    @Override
    public MenuItem findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item", id));
    }

    @Override
    public MenuItem update(Long userId, MenuItem menuItem) {
        MenuItem menu = repository
                .findById(menuItem.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item", menuItem.getId()));


        checkOwner(userId, menu.getRestaurantId());

        String newName = menuItem.getName();
        Long price = menuItem.getPriceInCents();

        if (newName == null || newName.isBlank()) {
            throw new BusinessRuleException("Menu item name must not be blank");
        }

        if (price == null || price < 0) {
            throw new BusinessRuleException("Menu item price must be greater than or equal to zero");
        }

        menu.setName(menuItem.getName());
        menu.setDescription(menuItem.getDescription());
        menu.setPriceInCents(menuItem.getPriceInCents());
        menu.setDineInOnly(menuItem.getDineInOnly());
        menu.setImagePath(menuItem.getImagePath());

        return repository.save(menu);
    }

    @Override
    public void delete(Long userId, Long id) {
        MenuItem menu = repository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item", id));

        checkOwner(userId, menu.getRestaurantId());

        repository.delete(menu);
    }

    private void checkOwner(Long userId, Long restaurantId) {
        Restaurant rest = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", restaurantId));

        if (!rest.getOwnerId().equals(userId)) {
            throw new ForbiddenOperationException(
                    "Only the restaurant owner can modify its menu"
            );
        }
    }
}
