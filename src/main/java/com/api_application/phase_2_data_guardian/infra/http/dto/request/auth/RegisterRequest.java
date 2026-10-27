package com.api_application.phase_2_data_guardian.infra.http.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Name is required")
        @Size(
                min = 2,
                max = 120,
                message = "Name must contain between 2 and 120 characters"
        )
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                max = 64,
                message = "Password must contain between 8 and 64 characters"
        )
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9\\s])\\S+$",
                message = "Password must contain an uppercase letter, a lowercase letter, a number and a special character, and must not contain spaces"
        )
        String password
) {
}
