package com.api_application.phase_2_data_guardian.domain.services;

import com.api_application.phase_2_data_guardian.application.repository.UserRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.application.usecase.UserTypeUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.domain.exception.BusinessRuleException;
import com.api_application.phase_2_data_guardian.domain.exception.ConflictException;
import com.api_application.phase_2_data_guardian.domain.exception.ForbiddenOperationException;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class UserTypeService implements UserTypeUseCase {
    private final UserTypeRepository repository;

    private final UserRepository userRepository;

    public UserTypeService(UserTypeRepository repository,
            UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public UserType create(Long authenticatedUserId, UserType userType) {
        checkManager(authenticatedUserId);
        String name = normalizeName(userType.getName());
        if (repository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("A user type with this name already exists");
        }
        userType.setName(name);
        return repository.save(userType);
    }

    @Override
    public Page<UserType> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public UserType findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User type", id));
    }

    @Override
    public UserType update(Long authenticatedUserId, Long id, UserType userType) {
        checkManager(authenticatedUserId);
        UserType existing = findById(id);
        String name = normalizeName(userType.getName());
        repository.findByNameIgnoreCase(name)
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new ConflictException("A user type with this name already exists");
                });
        if (!existing.getName().equals(name) && userRepository.existsByUserTypeId(id)) {
            throw new ConflictException("Cannot rename a user type associated with users");
        }
        existing.setName(name);
        return repository.save(existing);
    }

    @Override
    public void delete(Long authenticatedUserId, Long id) {
        checkManager(authenticatedUserId);
        UserType type = findById(id);
        if (userRepository.existsByUserTypeId(id)) {
            throw new ConflictException("User type is associated with users");
        }
        repository.delete(type);
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessRuleException("User type name must not be blank");
        }
        return name.trim().toUpperCase(Locale.ROOT);
    }
    private void checkManager(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        var type = findById(user.getUserTypeId());
        if (!"RESTAURANT_OWNER".equals(type.getName())) {
            throw new ForbiddenOperationException(
                    "Only RESTAURANT_OWNER can manage the user type catalog");
        }
    }
}
