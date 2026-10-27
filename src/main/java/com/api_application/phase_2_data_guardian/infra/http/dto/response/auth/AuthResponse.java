package com.api_application.phase_2_data_guardian.infra.http.dto.response.auth;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long expiresIn,
        AuthenticatedUserResponse user
) {
}
