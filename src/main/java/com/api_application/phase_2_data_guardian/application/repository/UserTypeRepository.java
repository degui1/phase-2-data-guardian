package com.api_application.phase_2_data_guardian.application.repository;

import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserTypeRepository {
    UserType save(UserType userType);
    Optional<UserType> findById(Long id);
    Optional<UserType> findByNameIgnoreCase(String name);
    Page<UserType> findAll(Pageable pageable);
    boolean existsByNameIgnoreCase(String name);
    void delete(UserType userType);
}
