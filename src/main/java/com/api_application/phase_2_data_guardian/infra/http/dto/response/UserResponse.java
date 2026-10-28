package com.api_application.phase_2_data_guardian.infra.http.dto.response;

import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        Long userTypeId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
