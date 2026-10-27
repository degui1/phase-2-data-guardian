package com.api_application.phase_2_data_guardian.domain.services;

import com.api_application.phase_2_data_guardian.application.repository.UserRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.application.usecase.UserUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.User;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;

public class UserService implements UserUseCase {
    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;

    public UserService(UserRepository userRepository, UserTypeRepository userTypeRepository) {
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
    }

    @Override
    public UserType assignUserType(Long userId, Long userTypeId) {
        User user = findUser(userId);
        UserType userType = findType(userTypeId);
        user.setUserTypeId(userType.getId());
        userRepository.save(user);
        return userType;
    }

    @Override
    public UserType findUserType(Long userId) {
        User user = findUser(userId);
        return findType(user.getUserTypeId());
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
