package com.api_application.phase_2_data_guardian.infra.http.dto.response.common;

import java.util.List;

public record OpeningHoursResponse(
        List<OpeningPeriodResponse> monday,
        List<OpeningPeriodResponse> tuesday,
        List<OpeningPeriodResponse> wednesday,
        List<OpeningPeriodResponse> thursday,
        List<OpeningPeriodResponse> friday,
        List<OpeningPeriodResponse> saturday,
        List<OpeningPeriodResponse> sunday
) {
}
