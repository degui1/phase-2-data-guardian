package com.api_application.phase_2_data_guardian.infra.http.dto.response;

import java.time.OffsetDateTime;

public record MenuItemResponse(
        Long id,
        Long restaurantId,
        String name,
        String description,
        Long priceInCents,
        Boolean dineInOnly,
        String imagePath,
        OffsetDateTime createdAt,
        OffsetDateTime  updatedAt
) {
}
