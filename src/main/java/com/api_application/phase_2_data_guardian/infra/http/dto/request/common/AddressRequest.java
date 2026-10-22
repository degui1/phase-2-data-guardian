package com.api_application.phase_2_data_guardian.infra.http.dto.request.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = "Street is required")
        @Size(max = 150, message = "Street must contain at most 150 characters")
        String street,

        @NotBlank(message = "Number is required")
        @Size(max = 20, message = "Number must contain at most 20 characters")
        String number,

        @Size(max = 100, message = "Complement must contain at most 100 characters")
        String complement,

        @NotBlank(message = "Neighborhood is required")
        @Size(max = 100, message = "Neighborhood must contain at most 100 characters")
        String neighborhood,

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must contain at most 100 characters")
        String city,

        @NotBlank(message = "State is required")
        @Pattern(
                regexp = "^[A-Za-z]{2}$",
                message = "State must contain exactly two letters"
        )
        String state,

        @NotBlank(message = "ZIP code is required")
        @Pattern(
                regexp = "^\\d{5}-?\\d{3}$",
                message = "ZIP code must use the format 00000-000"
        )
        String zipCode
) {
}
