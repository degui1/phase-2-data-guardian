package com.api_application.phase_2_data_guardian.infra.http.dto.request.usertype;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AssignUserTypeRequest(
        @NotNull(message = "User type ID is required")
        @Positive(message = "User type ID must be positive")
        Long userTypeId
) {}
