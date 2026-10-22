package com.api_application.phase_2_data_guardian.infra.http.controllers;

import com.api_application.phase_2_data_guardian.infra.http.dto.request.usertype.CreateUserTypeRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.usertype.UpdateUserTypeRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.UserTypeResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-types")
public class UserTypeController {
    @PostMapping
    public ResponseEntity<UserTypeResponse> create(@Valid @RequestBody CreateUserTypeRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping
    public ResponseEntity<Page<UserTypeResponse>> findAll(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{userTypeId}")
    public ResponseEntity<UserTypeResponse> findById(@PathVariable Long userTypeId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PutMapping("/{userTypeId}")
    public ResponseEntity<UserTypeResponse> update(
            @PathVariable Long userTypeId, @Valid @RequestBody UpdateUserTypeRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{userTypeId}")
    public ResponseEntity<Void> delete(@PathVariable Long userTypeId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
