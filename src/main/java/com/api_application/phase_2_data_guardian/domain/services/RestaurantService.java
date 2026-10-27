package com.api_application.phase_2_data_guardian.domain.services;

import com.api_application.phase_2_data_guardian.application.repository.RestaurantRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserRepository;
import com.api_application.phase_2_data_guardian.application.usecase.RestaurantUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.domain.exception.BusinessRuleException;
import com.api_application.phase_2_data_guardian.domain.exception.ForbiddenOperationException;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class RestaurantService implements RestaurantUseCase {
    private final RestaurantRepository repository;
    private final UserRepository userRepository;

    public RestaurantService(RestaurantRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public Restaurant create(Restaurant restaurant) {
        validate(restaurant);
        userRepository.findById(restaurant.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner", restaurant.getOwnerId()));
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
        checkOwner(authenticatedUserId, existing);
        validate(data);
        existing.setName(data.getName().trim());
        existing.setAddress(data.getAddress());
        existing.setCuisineType(data.getCuisineType().trim());
        existing.setOpeningHours(data.getOpeningHours());
        return repository.save(existing);
    }

    @Override
    public void delete(Long authenticatedUserId, Long id) {
        Restaurant restaurant = findById(id);
        checkOwner(authenticatedUserId, restaurant);
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
        if (restaurant.getOwnerId() == null) {
            throw new BusinessRuleException("Restaurant owner is required");
        }
    }

    private void checkOwner(Long userId, Restaurant restaurant) {
        if (userId == null || !userId.equals(restaurant.getOwnerId())) {
            throw new ForbiddenOperationException("Only the restaurant owner can modify the restaurant");
        }
    }
}
