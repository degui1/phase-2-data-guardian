package com.api_application.phase_2_data_guardian.domain.services;

import com.api_application.phase_2_data_guardian.application.repository.RestaurantRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.application.usecase.UserUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.User;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.domain.exception.ConflictException;
import com.api_application.phase_2_data_guardian.domain.exception.ForbiddenOperationException;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import java.util.Set;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.api_application.phase_2_data_guardian.application.model.user.UpdateUserCommand;
import com.api_application.phase_2_data_guardian.domain.exception.BusinessRuleException;

public class UserService implements UserUseCase {
    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;

    private final RestaurantRepository restaurantRepository;

    public UserService(UserRepository userRepository, UserTypeRepository userTypeRepository,
            RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
    }

    @Override
    public UserType assignUserType(Long authenticatedUserId, Long userId, Long userTypeId) {
        if (authenticatedUserId == null || !authenticatedUserId.equals(userId)) {
            throw new ForbiddenOperationException(
                    "Users may only change their own type");
        }
        User user = findUser(userId);
        UserType userType = findType(userTypeId);
        if (!Set.of("CUSTOMER", "RESTAURANT_OWNER").contains(userType.getName())) {
            throw new ForbiddenOperationException(
                    "Self-service type changes are limited to CUSTOMER and RESTAURANT_OWNER");
        }
        if (!"RESTAURANT_OWNER".equals(userType.getName()) && restaurantRepository.existsByOwnerId(userId)) {
            throw new ConflictException(
                    "Users who own restaurants must remain RESTAURANT_OWNER");
        }
        user.setUserTypeId(userType.getId());
        userRepository.save(user);
        return userType;
    }

    @Override
    public UserType findUserType(Long userId) {
        User user = findUser(userId);
        return findType(user.getUserTypeId());
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        if (pageable.getSort().stream().anyMatch(order -> !Set.of("id", "name").contains(order.getProperty()))) {
            throw new BusinessRuleException("User directory can only be sorted by id or name");
        }
        return userRepository.findAll(pageable);
    }

    @Override
    public User findById(Long authenticatedUserId, Long userId) {
        ensureOwnAccount(authenticatedUserId, userId);
        return findUser(userId);
    }

    @Override
    public User update(Long authenticatedUserId, Long userId, UpdateUserCommand command) {
        ensureOwnAccount(authenticatedUserId, userId);
        User user = findUser(userId);
        if (command == null || command.name() == null || command.name().isBlank()
                || command.email() == null || command.email().isBlank()) {
            throw new BusinessRuleException("User name and email are required");
        }
        String email = command.email().trim().toLowerCase(Locale.ROOT);
        userRepository.findByEmailIgnoreCase(email)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new ConflictException("Email already in use");
                });
        user.setName(command.name().trim());
        user.setEmail(email);
        return userRepository.save(user);
    }

    @Override
    public void delete(Long authenticatedUserId, Long userId) {
        ensureOwnAccount(authenticatedUserId, userId);
        User user = findUser(userId);
        if (restaurantRepository.existsByOwnerId(userId)) {
            throw new ConflictException("Remove the user's restaurants before deleting the account");
        }
        userRepository.delete(user);
    }

    private void ensureOwnAccount(Long authenticatedUserId, Long userId) {
        if (authenticatedUserId == null || !authenticatedUserId.equals(userId)) {
            throw new ForbiddenOperationException("Users may only access or modify their own account");
        }
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private UserType findType(Long id) {
        return userTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User type", id));
    }
}
