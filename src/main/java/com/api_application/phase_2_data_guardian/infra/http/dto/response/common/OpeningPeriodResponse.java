package com.api_application.phase_2_data_guardian.infra.http.dto.response.common;

import java.time.LocalTime;

public record OpeningPeriodResponse(
        LocalTime opensAt,
        LocalTime closesAt
) {
}
