package com.api_application.phase_2_data_guardian.infra.configuration;

import com.api_application.phase_2_data_guardian.application.repository.MenuItemRepository;
import com.api_application.phase_2_data_guardian.application.repository.RestaurantRepository;
import com.api_application.phase_2_data_guardian.application.usecase.MenuItemUseCase;
import com.api_application.phase_2_data_guardian.domain.services.MenuItemService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MenuItemConfiguration {

    @Bean
    public MenuItemUseCase menuItemUseCase(
            MenuItemRepository menuItemRepository,
            RestaurantRepository restaurantRepository
    ) {
        return new MenuItemService(menuItemRepository, restaurantRepository);
    }
}
