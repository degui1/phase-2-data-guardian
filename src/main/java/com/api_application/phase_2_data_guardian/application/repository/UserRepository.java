package com.api_application.phase_2_data_guardian.application.repository;

import com.api_application.phase_2_data_guardian.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    void delete(User user);
}
