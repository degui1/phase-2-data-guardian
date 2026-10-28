package com.api_application.phase_2_data_guardian.infra.configuration;

import com.api_application.phase_2_data_guardian.application.repository.MenuItemRepository;
import com.api_application.phase_2_data_guardian.application.repository.RestaurantRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.application.usecase.RestaurantUseCase;
import com.api_application.phase_2_data_guardian.domain.services.RestaurantService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestaurantConfiguration {
    @Bean
    public RestaurantUseCase restaurantUseCase(RestaurantRepository repository, UserRepository userRepository, UserTypeRepository userTypeRepository, MenuItemRepository menuItemRepository) {
        return new RestaurantService(repository, userRepository, userTypeRepository, menuItemRepository);
    }
}
