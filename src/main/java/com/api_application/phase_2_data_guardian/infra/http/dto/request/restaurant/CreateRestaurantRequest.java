package com.api_application.phase_2_data_guardian.infra.http.dto.request.restaurant;

import com.api_application.phase_2_data_guardian.infra.http.dto.request.common.AddressRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.common.OpeningHoursRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateRestaurantRequest(
        @NotBlank(message = "Name is required")
        @Size(
                min = 2,
                max = 120,
                message = "Name must contain between 2 and 120 characters"
        )
        String name,

        @NotNull(message = "Address is required")
        @Valid
        AddressRequest address,

        @NotBlank(message = "Cuisine type is required")
        @Size(
                min = 2,
                max = 80,
                message = "Cuisine type must contain between 2 and 80 characters"
        )
        String cuisineType,

        @NotNull(message = "Opening hours are required")
        @Valid
        OpeningHoursRequest openingHours,

        @NotNull(message = "Owner ID is required")
        @Positive(message = "Owner ID must be positive")
        Long ownerId
) {}
