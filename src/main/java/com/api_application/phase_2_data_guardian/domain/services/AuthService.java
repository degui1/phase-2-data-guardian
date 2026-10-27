package com.api_application.phase_2_data_guardian.domain.services;

import com.api_application.phase_2_data_guardian.application.model.auth.AuthenticatedUser;
import com.api_application.phase_2_data_guardian.application.model.auth.AuthenticationResult;
import com.api_application.phase_2_data_guardian.application.model.auth.LoginCommand;
import com.api_application.phase_2_data_guardian.application.model.auth.RegisterCommand;
import com.api_application.phase_2_data_guardian.application.model.auth.TokenData;
import com.api_application.phase_2_data_guardian.application.repository.UserRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.application.security.PasswordHasher;
import com.api_application.phase_2_data_guardian.application.security.TokenProvider;
import com.api_application.phase_2_data_guardian.application.usecase.AuthUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.User;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.domain.exception.ConflictException;

import java.util.Locale;
import java.util.Optional;

public class AuthService implements AuthUseCase {

    private static final String DEFAULT_USER_TYPE_NAME = "CUSTOMER";
    private static final String TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final PasswordHasher passwordHasher;
    private final TokenProvider tokenProvider;

    public AuthService(
            UserRepository userRepository,
            UserTypeRepository userTypeRepository,
            PasswordHasher passwordHasher,
            TokenProvider tokenProvider
    ) {
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthenticationResult register(RegisterCommand command) {
        String normalizedEmail = command
                .email()
                .trim()
                .toLowerCase(Locale.ROOT);

        Optional<User> existingUser = userRepository
                .findByEmailIgnoreCase(normalizedEmail);

        if (existingUser.isPresent()) {
            throw new ConflictException("Email already in use");
        }

        UserType defaultUserType = userTypeRepository
                .findByNameIgnoreCase(DEFAULT_USER_TYPE_NAME)
                .orElseThrow(() -> new IllegalStateException(
                        "Default user type is not configured: "
                                + DEFAULT_USER_TYPE_NAME
                ));

        String encodedPassword = passwordHasher.hash(command.password());

        User newUser = new User(
                command.name().trim(),
                normalizedEmail,
                encodedPassword,
                defaultUserType.getId()
        );

        User savedUser = userRepository.save(newUser);

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                defaultUserType.getId(),
                defaultUserType.getName()
        );

        TokenData token = tokenProvider.generate(authenticatedUser);

        return new AuthenticationResult(
                token.accessToken(),
                TOKEN_TYPE,
                token.expiresIn(),
                authenticatedUser
        );
    }

    @Override
    public AuthenticationResult login(LoginCommand command) {
        return null;
    }

    @Override
    public AuthenticatedUser findAuthenticatedUser(Long userId) {
        return null;
    }
}
