package com.api_application.phase_2_data_guardian.infra.configuration;

import com.api_application.phase_2_data_guardian.application.repository.UserRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.application.security.PasswordHasher;
import com.api_application.phase_2_data_guardian.application.security.TokenProvider;
import com.api_application.phase_2_data_guardian.application.usecase.AuthUseCase;
import com.api_application.phase_2_data_guardian.domain.services.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfiguration {

    @Bean
    public AuthUseCase authUseCase(
            UserRepository userRepository,
            UserTypeRepository userTypeRepository,
            PasswordHasher passwordHasher,
            TokenProvider tokenProvider
    ) {
        return new AuthService(
                userRepository,
                userTypeRepository,
                passwordHasher,
                tokenProvider
        );
    }
}
