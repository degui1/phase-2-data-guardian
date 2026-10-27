package com.api_application.phase_2_data_guardian.infra.http.controllers;

import com.api_application.phase_2_data_guardian.application.usecase.UserUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.usertype.AssignUserTypeRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.UserTypeResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PutMapping("/{userId}/user-type")
    public ResponseEntity<UserTypeResponse> assignUserType(
            @PathVariable Long userId,
            @Valid @RequestBody AssignUserTypeRequest request
    ) {
        return ResponseEntity.ok(toResponse(userUseCase.assignUserType(userId, request.userTypeId())));
    }

    @GetMapping("/{userId}/user-type")
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
