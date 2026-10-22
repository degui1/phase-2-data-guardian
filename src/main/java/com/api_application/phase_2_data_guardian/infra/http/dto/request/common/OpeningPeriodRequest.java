package com.api_application.phase_2_data_guardian.infra.http.dto.request.common;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record OpeningPeriodRequest(
        @NotNull(message = "Opening time is required")
        LocalTime opensAt,

        @NotNull(message = "Closing time is required")
        LocalTime closesAt
) {
}
