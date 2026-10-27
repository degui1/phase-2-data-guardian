package com.api_application.phase_2_data_guardian.application.usecase;

import com.api_application.phase_2_data_guardian.domain.entity.UserType;

public interface UserUseCase {
    UserType assignUserType(Long userId, Long userTypeId);
    UserType findUserType(Long userId);
}
