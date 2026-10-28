package com.api_application.phase_2_data_guardian.infra.http.mapper;

import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.MenuItemResponse;
import org.springframework.stereotype.Component;

@Component
public class MenuItemResponseMapper {

    public MenuItemResponse toResponse(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getRestaurantId(),
                item.getName(),
                item.getDescription(),
                item.getPriceInCents(),
                item.getDineInOnly(),
                item.getImagePath(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
