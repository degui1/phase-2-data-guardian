package com.api_application.phase_2_data_guardian.application.repository;

import com.api_application.phase_2_data_guardian.domain.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmailIgnoreCase(String email);

    Page<User> findAll(Pageable pageable);

    void delete(User user);
    boolean existsByUserTypeId(Long userTypeId);
}
