package com.api_application.phase_2_data_guardian.application.usecase;

import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserTypeUseCase {
    UserType create(UserType userType);
    Page<UserType> findAll(Pageable pageable);
    UserType findById(Long id);
    UserType update(Long id, UserType userType);
    void delete(Long id);
}
