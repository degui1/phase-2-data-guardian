package com.api_application.phase_2_data_guardian.application.model.auth;

public record RegisterCommand(String name, String email, String password) {
}
