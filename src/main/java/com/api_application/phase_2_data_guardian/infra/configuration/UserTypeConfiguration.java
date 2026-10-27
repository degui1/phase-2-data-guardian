package com.api_application.phase_2_data_guardian.infra.configuration;

import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.application.usecase.UserTypeUseCase;
import com.api_application.phase_2_data_guardian.domain.services.UserTypeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserTypeConfiguration {
    @Bean
    public UserTypeUseCase userTypeUseCase(UserTypeRepository repository) {
        return new UserTypeService(repository);
    }
}
