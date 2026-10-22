package com.api_application.phase_2_data_guardian.infra.http.dto.request.usertype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserTypeRequest(
        @NotBlank(message = "Name is required")
        @Size(
                min = 2,
                max = 50,
                message = "Name must contain between 2 and 50 characters"
        )
        String name
) {}