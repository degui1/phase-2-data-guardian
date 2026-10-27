package com.api_application.phase_2_data_guardian.infra.http.controllers;

import com.api_application.phase_2_data_guardian.application.usecase.UserTypeUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.usertype.CreateUserTypeRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.usertype.UpdateUserTypeRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.UserTypeResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/user-types")
public class UserTypeController {
    private final UserTypeUseCase userTypeUseCase;

    public UserTypeController(UserTypeUseCase userTypeUseCase) {
        this.userTypeUseCase = userTypeUseCase;
    }

    @PostMapping
    public ResponseEntity<UserTypeResponse> create(@Valid @RequestBody CreateUserTypeRequest request) {
        UserType created = userTypeUseCase.create(new UserType(request.name()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{userTypeId}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(created));
    }

    @GetMapping
    public ResponseEntity<Page<UserTypeResponse>> findAll(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(userTypeUseCase.findAll(pageable).map(this::toResponse));
    }

    @GetMapping("/{userTypeId}")
    public ResponseEntity<UserTypeResponse> findById(@PathVariable Long userTypeId) {
        return ResponseEntity.ok(toResponse(userTypeUseCase.findById(userTypeId)));
    }

    @PutMapping("/{userTypeId}")
    public ResponseEntity<UserTypeResponse> update(
            @PathVariable Long userTypeId,
            @Valid @RequestBody UpdateUserTypeRequest request
    ) {
        UserType updated = userTypeUseCase.update(userTypeId, new UserType(request.name()));
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{userTypeId}")
    public ResponseEntity<Void> delete(@PathVariable Long userTypeId) {
        userTypeUseCase.delete(userTypeId);
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
