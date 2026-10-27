package com.api_application.phase_2_data_guardian.infra.configuration;

import com.api_application.phase_2_data_guardian.application.repository.UserRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.application.usecase.UserUseCase;
import com.api_application.phase_2_data_guardian.domain.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfiguration {
    @Bean
    public UserUseCase userUseCase(UserRepository userRepository, UserTypeRepository userTypeRepository) {
        return new UserService(userRepository, userTypeRepository);
    }
}
