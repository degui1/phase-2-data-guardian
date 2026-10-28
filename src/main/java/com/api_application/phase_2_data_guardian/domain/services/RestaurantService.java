package com.api_application.phase_2_data_guardian.domain.services;

import com.api_application.phase_2_data_guardian.application.repository.MenuItemRepository;
import com.api_application.phase_2_data_guardian.application.repository.RestaurantRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.application.usecase.RestaurantUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.OpeningPeriod;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.domain.exception.BusinessRuleException;
import com.api_application.phase_2_data_guardian.domain.exception.ConflictException;
import com.api_application.phase_2_data_guardian.domain.exception.ForbiddenOperationException;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class RestaurantService implements RestaurantUseCase {
    private final RestaurantRepository repository;
    private final UserRepository userRepository;

    private final UserTypeRepository userTypeRepository;
    private final MenuItemRepository menuItemRepository;

    public RestaurantService(RestaurantRepository repository, UserRepository userRepository,
            UserTypeRepository userTypeRepository,
            MenuItemRepository menuItemRepository) {
        this.userTypeRepository = userTypeRepository;
        this.menuItemRepository = menuItemRepository;
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public Restaurant create(Restaurant restaurant) {
        validate(restaurant);
        ensureEligibleOwner(restaurant.getOwnerId());
        return repository.save(restaurant);
    }

    @Override
    public Page<Restaurant> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Restaurant findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", id));
    }

    @Override
    public Restaurant update(Long authenticatedUserId, Long id, Restaurant data) {
        Restaurant existing = findById(id);
        ensureRestaurantOwnership(authenticatedUserId, existing);
        validate(data);
        ensureEligibleOwner(existing.getOwnerId());
        existing.setName(data.getName().trim());
        existing.setAddress(data.getAddress());
        existing.setCuisineType(data.getCuisineType().trim());
        existing.setOpeningHours(data.getOpeningHours());
        return repository.save(existing);
    }

    @Override
    public void delete(Long authenticatedUserId, Long id) {
        Restaurant restaurant = findById(id);
        ensureRestaurantOwnership(authenticatedUserId, restaurant);
        if (menuItemRepository.existsByRestaurantId(id)) {
            throw new ConflictException(
                    "Remove the restaurant menu items before deleting the restaurant");
        }
        repository.delete(restaurant);
    }

    private void validate(Restaurant restaurant) {
        if (restaurant.getName() == null || restaurant.getName().isBlank()) {
            throw new BusinessRuleException("Restaurant name must not be blank");
        }
        if (restaurant.getCuisineType() == null || restaurant.getCuisineType().isBlank()) {
            throw new BusinessRuleException("Cuisine type must not be blank");
        }
        if (restaurant.getAddress() == null) {
            throw new BusinessRuleException("Restaurant address is required");
        }
        if (restaurant.getOpeningHours() == null) {
            throw new BusinessRuleException("Opening hours are required");
        }
        var hours = restaurant.getOpeningHours();
        for (var periods : Arrays.asList(hours.getMonday(), hours.getTuesday(),
                hours.getWednesday(), hours.getThursday(), hours.getFriday(), hours.getSaturday(), hours.getSunday())) {
            if (periods == null) {
                throw new BusinessRuleException("Opening periods must not be null; use an empty list for closed days");
            }
            for (var period : periods) {
                if (period == null || period.getOpensAt() == null || period.getClosesAt() == null
                        || !period.getOpensAt().isBefore(period.getClosesAt())) {
                    throw new BusinessRuleException("Opening time must be before closing time");
                }
            }
            var sorted = periods.stream().sorted(Comparator.comparing(
                    OpeningPeriod::getOpensAt)).toList();
            for (int i = 1; i < sorted.size(); i++) {
                if (sorted.get(i).getOpensAt().isBefore(sorted.get(i - 1).getClosesAt())) {
                    throw new BusinessRuleException("Opening periods on the same day must not overlap");
                }
            }
        }
        if (restaurant.getOwnerId() == null) {
            throw new BusinessRuleException("Restaurant owner is required");
        }
    }

    private void ensureRestaurantOwnership(Long userId, Restaurant restaurant) {
        if (userId == null || !userId.equals(restaurant.getOwnerId())) {
            throw new ForbiddenOperationException("Only the restaurant owner can modify the restaurant");
        }
    }
    private void ensureEligibleOwner(Long ownerId) {
        var owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", ownerId));
        var type = userTypeRepository.findById(owner.getUserTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("User type", owner.getUserTypeId()));
        if (!"RESTAURANT_OWNER".equals(type.getName())) {
            throw new ForbiddenOperationException("Restaurant owner must have type RESTAURANT_OWNER");
        }
    }
}
