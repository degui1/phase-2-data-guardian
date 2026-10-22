package com.api_application.phase_2_data_guardian.infra.http.controllers;

import com.api_application.phase_2_data_guardian.infra.http.dto.request.usertype.AssignUserTypeRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.UserTypeResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @PutMapping("/{userId}/user-type")
    public ResponseEntity<UserTypeResponse> assignUserType(
            @PathVariable Long userId, @Valid @RequestBody AssignUserTypeRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{userId}/user-type")
    public ResponseEntity<UserTypeResponse> findUserType(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
