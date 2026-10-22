package com.api_application.phase_2_data_guardian.infra.http.dto.response;

import com.api_application.phase_2_data_guardian.infra.http.dto.response.common.AddressResponse;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.common.OpeningHoursResponse;

import java.time.OffsetDateTime;

public record RestaurantResponse(
        Long id,
        String name,
        AddressResponse address,
        String cuisineType,
        OpeningHoursResponse openingHours,
        Long ownerId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
