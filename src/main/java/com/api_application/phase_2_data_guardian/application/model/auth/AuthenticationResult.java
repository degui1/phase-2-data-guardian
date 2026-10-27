package com.api_application.phase_2_data_guardian.application.model.auth;

public record AuthenticationResult(
        String accessToken,
        String tokenType,
        Long expiresIn,
        AuthenticatedUser user
) {
}
