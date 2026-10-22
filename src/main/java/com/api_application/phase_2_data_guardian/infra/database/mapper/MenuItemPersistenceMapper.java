package com.api_application.phase_2_data_guardian.infra.database.mapper;

import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import com.api_application.phase_2_data_guardian.infra.database.entity.MenuItemJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.entity.RestaurantJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MenuItemPersistenceMapper {

    public MenuItem toDomain(MenuItemJpaEntity entity) {
        return new MenuItem(
                entity.getId(),
                entity.getRestaurant().getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPriceInCents(),
                entity.getDineInOnly(),
                entity.getImagePath(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public MenuItemJpaEntity toJpaEntity(
            MenuItem domain,
            RestaurantJpaEntity restaurant
    ) {
        return new MenuItemJpaEntity(
                domain.getId(),
                restaurant,
                domain.getName(),
                domain.getDescription(),
                domain.getPriceInCents(),
                domain.getDineInOnly(),
                domain.getImagePath(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
