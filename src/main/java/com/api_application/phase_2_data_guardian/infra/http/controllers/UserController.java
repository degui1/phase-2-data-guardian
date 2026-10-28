package com.api_application.phase_2_data_guardian.infra.http.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.api_application.phase_2_data_guardian.application.usecase.UserUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.usertype.AssignUserTypeRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.UserTypeResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuários")
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PutMapping("/{userId}/user-type")
    @Operation(summary = "Alterar o próprio tipo", description = "Somente o próprio usuário, entre CUSTOMER e RESTAURANT_OWNER. Quem possui restaurantes não pode virar CUSTOMER.")
    public ResponseEntity<UserTypeResponse> assignUserType(
            @Parameter(hidden = true) Authentication authentication,
            @PathVariable Long userId,
            @Valid @RequestBody AssignUserTypeRequest request
    ) {
        return ResponseEntity.ok(toResponse(userUseCase.assignUserType(Long.valueOf(authentication.getName()), userId, request.userTypeId())));
    }

    @GetMapping("/{userId}/user-type")
    @Operation(summary = "Consultar tipo de um usuário", description = "Exige autenticação e retorna o tipo associado ao usuário informado.")
    public ResponseEntity<UserTypeResponse> findUserType(@PathVariable Long userId) {
        return ResponseEntity.ok(toResponse(userUseCase.findUserType(userId)));
    }

    private UserTypeResponse toResponse(UserType userType) {
        return new UserTypeResponse(
                userType.getId(),
                userType.getName(),
                userType.getCreatedAt(),
                userType.getUpdatedAt()
        );
    }
}
