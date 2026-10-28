package com.api_application.phase_2_data_guardian.infra.http.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

import com.api_application.phase_2_data_guardian.application.usecase.UserTypeUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.usertype.CreateUserTypeRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.usertype.UpdateUserTypeRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.UserTypeResponse;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Tipos de usuário")
@RestController
@RequestMapping("/user-types")
public class UserTypeController {
    private final UserTypeUseCase userTypeUseCase;

    public UserTypeController(UserTypeUseCase userTypeUseCase) {
        this.userTypeUseCase = userTypeUseCase;
    }

    @PostMapping
    @Operation(summary = "Criar tipo de usuário", description = "Somente RESTAURANT_OWNER. Nome normalizado para maiúsculas; duplicatas retornam 409.")
    @ApiResponse(responseCode = "201", description = "Recurso criado; URL no header Location")
    public ResponseEntity<UserTypeResponse> create(@Parameter(hidden = true) Authentication authentication, @Valid @RequestBody CreateUserTypeRequest request) {
        UserType created = userTypeUseCase.create(Long.valueOf(authentication.getName()), new UserType(request.name()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{userTypeId}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(created));
    }

    @GetMapping
    @Operation(summary = "Listar tipos de usuário", description = "Paginação: page inicia em zero; size padrão 10; sort padrão name,asc.")
    public ResponseEntity<Page<UserTypeResponse>> findAll(
            @ParameterObject @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(userTypeUseCase.findAll(pageable).map(this::toResponse));
    }

    @GetMapping("/{userTypeId}")
    @Operation(summary = "Consultar tipo de usuário", description = "Retorna o tipo identificado pelo ID.")
    public ResponseEntity<UserTypeResponse> findById(@PathVariable Long userTypeId) {
        return ResponseEntity.ok(toResponse(userTypeUseCase.findById(userTypeId)));
    }

    @PutMapping("/{userTypeId}")
    @Operation(summary = "Atualizar tipo de usuário", description = "Somente RESTAURANT_OWNER. Tipo associado não pode ser renomeado para outro nome.")
    public ResponseEntity<UserTypeResponse> update(
            @Parameter(hidden = true) Authentication authentication,
            @PathVariable Long userTypeId,
            @Valid @RequestBody UpdateUserTypeRequest request
    ) {
        UserType updated = userTypeUseCase.update(Long.valueOf(authentication.getName()), userTypeId, new UserType(request.name()));
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{userTypeId}")
    @Operation(summary = "Excluir tipo de usuário", description = "Somente RESTAURANT_OWNER. Tipo associado a usuários retorna 409.")
    @ApiResponse(responseCode = "204", description = "Recurso excluído", content = @io.swagger.v3.oas.annotations.media.Content)
    public ResponseEntity<Void> delete(@Parameter(hidden = true) Authentication authentication, @PathVariable Long userTypeId) {
        userTypeUseCase.delete(Long.valueOf(authentication.getName()), userTypeId);
        return ResponseEntity.noContent().build();
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
