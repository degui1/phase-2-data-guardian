package com.api_application.phase_2_data_guardian.application.security;

public interface PasswordHasher {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}
