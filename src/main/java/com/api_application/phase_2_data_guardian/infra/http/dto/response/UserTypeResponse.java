package com.api_application.phase_2_data_guardian.infra.http.dto.response;

import java.time.OffsetDateTime;

public record UserTypeResponse(
        Long id,
        String name,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
