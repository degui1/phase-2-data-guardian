package com.api_application.phase_2_data_guardian.application.model.auth;

public record AuthenticatedUser(
        Long id,
        String name,
        String email,
        Long userTypeId,
        String userTypeName
) {
}
