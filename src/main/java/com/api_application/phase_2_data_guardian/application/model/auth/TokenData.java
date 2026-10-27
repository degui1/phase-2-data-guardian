package com.api_application.phase_2_data_guardian.application.model.auth;

public record TokenData(
        String accessToken,
        Long expiresIn
) {
}
