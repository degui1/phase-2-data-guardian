package com.api_application.phase_2_data_guardian.infra.http.controllers;

import com.api_application.phase_2_data_guardian.application.model.auth.AuthenticatedUser;
import com.api_application.phase_2_data_guardian.application.model.auth.AuthenticationResult;
import com.api_application.phase_2_data_guardian.application.model.auth.LoginCommand;
import com.api_application.phase_2_data_guardian.application.model.auth.RegisterCommand;
import com.api_application.phase_2_data_guardian.application.usecase.AuthUseCase;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.auth.LoginRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.auth.RegisterRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.auth.AuthResponse;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.auth.AuthenticatedUserResponse;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.auth.UserTypeSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        RegisterCommand command = new RegisterCommand(
                request.name(),
                request.email(),
                request.password()
        );

        AuthenticationResult result = authUseCase.register(command);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/{userId}")
                .buildAndExpand(result.user().id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(toResponse(result));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginCommand command = new LoginCommand(
                request.email(),
                request.password()
        );

        return ResponseEntity.ok(
                toResponse(authUseCase.login(command))
        );
    }

    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserResponse> me(
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(
                toResponse(authUseCase.findAuthenticatedUser(userId))
        );
    }

    private AuthResponse toResponse(AuthenticationResult result) {
        return new AuthResponse(
                result.accessToken(),
                result.tokenType(),
                result.expiresIn(),
                toResponse(result.user())
        );
    }

    private AuthenticatedUserResponse toResponse(AuthenticatedUser user) {
        return new AuthenticatedUserResponse(
                user.id(),
                user.name(),
                user.email(),
                new UserTypeSummaryResponse(
                        user.userTypeId(),
                        user.userTypeName()
                )
        );
    }
}
