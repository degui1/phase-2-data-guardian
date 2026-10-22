package com.api_application.phase_2_data_guardian.infra.http.controllers;

import com.api_application.phase_2_data_guardian.application.usecase.MenuItemUseCase;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.menuitem.CreateMenuItemRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.menuitem.UpdateMenuItemRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.MenuItemResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants/{restaurantId}/menu-items")
public class MenuItemController {

    private final MenuItemUseCase menuItemUseCase;

    @PostMapping
    public ResponseEntity<MenuItemResponse> create(
            @PathVariable Long restaurantId, @Valid @RequestBody CreateMenuItemRequest request) {

        menuItemUseCase.create()

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping
    public ResponseEntity<Page<MenuItemResponse>> findAll(
            @PathVariable Long restaurantId,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{menuItemId}")
    public ResponseEntity<MenuItemResponse> findById(
            @PathVariable Long restaurantId, @PathVariable Long menuItemId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PutMapping("/{menuItemId}")
    public ResponseEntity<MenuItemResponse> update(
            @PathVariable Long restaurantId, @PathVariable Long menuItemId,
            @Valid @RequestBody UpdateMenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{menuItemId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long restaurantId, @PathVariable Long menuItemId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
