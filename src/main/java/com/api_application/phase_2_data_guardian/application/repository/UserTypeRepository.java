package com.api_application.phase_2_data_guardian.application.repository;

import com.api_application.phase_2_data_guardian.domain.entity.UserType;

import java.util.List;
import java.util.Optional;

public interface UserTypeRepository {

    UserType save(UserType userType);

    Optional<UserType> findById(Long id);

    Optional<UserType> findByNameIgnoreCase(String name);

    List<UserType> findAll();

    boolean existsByNameIgnoreCase(String name);

    void delete(UserType userType);
}
