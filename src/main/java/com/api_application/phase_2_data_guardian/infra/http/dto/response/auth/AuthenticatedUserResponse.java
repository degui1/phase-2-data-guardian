package com.api_application.phase_2_data_guardian.infra.http.dto.response.auth;

public record AuthenticatedUserResponse(
        Long id,
        String name,
        String email,
        UserTypeSummaryResponse userType
) {
}
