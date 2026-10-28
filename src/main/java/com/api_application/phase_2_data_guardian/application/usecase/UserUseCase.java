package com.api_application.phase_2_data_guardian.application.usecase;

import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.domain.entity.User;
import com.api_application.phase_2_data_guardian.application.model.user.UpdateUserCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserUseCase {
    Page<User> findAll(Pageable pageable);
    User findById(Long authenticatedUserId, Long userId);
    User update(Long authenticatedUserId, Long userId, UpdateUserCommand command);
    void delete(Long authenticatedUserId, Long userId);

    UserType assignUserType(Long authenticatedUserId, Long userId, Long userTypeId);
    UserType findUserType(Long userId);
}
