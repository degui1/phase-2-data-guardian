package com.api_application.phase_2_data_guardian.domain.services;

import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.application.usecase.UserTypeUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.domain.exception.BusinessRuleException;
import com.api_application.phase_2_data_guardian.domain.exception.ConflictException;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class UserTypeService implements UserTypeUseCase {
    private final UserTypeRepository repository;

    public UserTypeService(UserTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserType create(UserType userType) {
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
    public UserType update(Long id, UserType userType) {
        UserType existing = findById(id);
        String name = normalizeName(userType.getName());
        repository.findByNameIgnoreCase(name)
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new ConflictException("A user type with this name already exists");
                });
        existing.setName(name);
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessRuleException("User type name must not be blank");
        }
        return name.trim().toUpperCase();
    }
}
