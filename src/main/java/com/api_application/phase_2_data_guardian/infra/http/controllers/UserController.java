package com.api_application.phase_2_data_guardian.infra.http.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.api_application.phase_2_data_guardian.application.usecase.UserUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.usertype.AssignUserTypeRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.UserTypeResponse;
import com.api_application.phase_2_data_guardian.application.model.user.UpdateUserCommand;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.user.UpdateUserRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.UserResponse;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.UserSummaryResponse;
import com.api_application.phase_2_data_guardian.infra.http.mapper.UserResponseMapper;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuários")
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserUseCase userUseCase;

    private final UserResponseMapper mapper;

    public UserController(UserUseCase userUseCase, UserResponseMapper mapper) {
        this.userUseCase = userUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Lista paginada com ID, nome e tipo. Não expõe email, senha ou hash. Ordenação somente por id ou name. Requer autenticação.")
    public ResponseEntity<Page<UserSummaryResponse>> findAll(
            @ParameterObject @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(userUseCase.findAll(pageable).map(mapper::toSummary));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Consultar o próprio usuário", description = "Somente o próprio usuário pode consultar os dados completos. Não retorna senha ou hash.")
    public ResponseEntity<UserResponse> findById(
            @Parameter(hidden = true) Authentication authentication, @PathVariable Long userId) {
        return ResponseEntity.ok(mapper.toResponse(userUseCase.findById(
                Long.valueOf(authentication.getName()), userId)));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Atualizar o próprio usuário", description = "Atualiza nome e email. Preserva senha, tipo e ID. Email de outra conta retorna 409.")
    public ResponseEntity<UserResponse> update(
            @Parameter(hidden = true) Authentication authentication, @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        var command = new UpdateUserCommand(request.name(), request.email());
        return ResponseEntity.ok(mapper.toResponse(userUseCase.update(
                Long.valueOf(authentication.getName()), userId, command)));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Excluir o próprio usuário", description = "Somente o próprio usuário. Conta com restaurantes associados retorna 409; remova os restaurantes antes.")
    @ApiResponse(responseCode = "204", description = "Usuário excluído", content = @io.swagger.v3.oas.annotations.media.Content)
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true) Authentication authentication, @PathVariable Long userId) {
        userUseCase.delete(Long.valueOf(authentication.getName()), userId);
        return ResponseEntity.noContent().build();
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
