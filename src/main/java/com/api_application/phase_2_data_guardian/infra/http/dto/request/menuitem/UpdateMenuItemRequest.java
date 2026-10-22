package com.api_application.phase_2_data_guardian.infra.http.dto.request.menuitem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateMenuItemRequest(
        @NotBlank(message = "Name is required")
        @Size(
                min = 2,
                max = 120,
                message = "Name must contain between 2 and 120 characters"
        )
        String name,

        @NotBlank(message = "Description is required")
        @Size(
                min = 3,
                max = 500,
                message = "Description must contain between 3 and 500 characters"
        )
        String description,

        @NotNull(message = "Price is required")
        @PositiveOrZero(message = "Price cannot be negative")
        Long priceInCents,

        @NotNull(message = "Dine-in-only indicator is required")
        Boolean dineInOnly,

        @NotBlank(message = "Image path is required")
        @Size(
                max = 500,
                message = "Image path must contain at most 500 characters"
        )
        String imagePath
) {
}
