package com.api_application.phase_2_data_guardian.infra.http.dto.request.common;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.util.List;

public record OpeningHoursRequest(
        @NotNull(message = "Monday opening hours are required")
        List<@NotNull @Valid OpeningPeriodRequest> monday,

        @NotNull(message = "Tuesday opening hours are required")
        List<@NotNull @Valid OpeningPeriodRequest> tuesday,

        @NotNull(message = "Wednesday opening hours are required")
        List<@NotNull @Valid OpeningPeriodRequest> wednesday,

        @NotNull(message = "Thursday opening hours are required")
        List<@NotNull @Valid OpeningPeriodRequest> thursday,

        @NotNull(message = "Friday opening hours are required")
        List<@NotNull @Valid OpeningPeriodRequest> friday,

        @NotNull(message = "Saturday opening hours are required")
        List<@NotNull @Valid OpeningPeriodRequest> saturday,

        @NotNull(message = "Sunday opening hours are required")
        List<@NotNull @Valid OpeningPeriodRequest> sunday
) {
}
