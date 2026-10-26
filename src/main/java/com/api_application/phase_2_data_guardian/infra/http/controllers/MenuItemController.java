package com.api_application.phase_2_data_guardian.infra.http.controllers;

import com.api_application.phase_2_data_guardian.application.usecase.MenuItemUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/restaurants/{restaurantId}/menu-items")
public class MenuItemController {

    private final MenuItemUseCase menuItemUseCase;

    public MenuItemController(MenuItemUseCase menuItemUseCase) {
        this.menuItemUseCase = menuItemUseCase;
    }

    @PostMapping
    public ResponseEntity<MenuItemResponse> create(
            @PathVariable Long restaurantId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateMenuItemRequest request
    ) {
        MenuItem menuItem = new MenuItem(
                restaurantId,
                request.name(),
                request.description(),
                request.priceInCents(),
                request.dineInOnly(),
                request.imagePath()
        );

        MenuItem createdMenuItem = menuItemUseCase.create(userId, menuItem);

        MenuItemResponse response = new MenuItemResponse(
                createdMenuItem.getId(),
                createdMenuItem.getRestaurantId(),
                createdMenuItem.getName(),
                createdMenuItem.getDescription(),
                createdMenuItem.getPriceInCents(),
                createdMenuItem.getDineInOnly(),
                createdMenuItem.getImagePath(),
                createdMenuItem.getCreatedAt(),
                createdMenuItem.getUpdatedAt()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{menuItemId}")
                .buildAndExpand(createdMenuItem.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<MenuItemResponse>> findAll(
            @PathVariable Long restaurantId,
            @PageableDefault(
                    size = 10,
                    sort = "name",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<MenuItemResponse> menuItems = menuItemUseCase
                .findByRestaurantId(restaurantId, pageable)
                .map((item) -> new MenuItemResponse(
                        item.getId(),
                        item.getRestaurantId(),
                        item.getName(),
                        item.getDescription(),
                        item.getPriceInCents(),
                        item.getDineInOnly(),
                        item.getImagePath(),
                        item.getCreatedAt(),
                        item.getUpdatedAt())
                );

        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/{menuItemId}")
    public ResponseEntity<MenuItemResponse> findById(
            @PathVariable Long restaurantId, @PathVariable Long menuItemId) {
        MenuItem menuItem = menuItemUseCase.findById(menuItemId);

        return ResponseEntity.ok(new MenuItemResponse(
                menuItem.getId(),
                menuItem.getRestaurantId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPriceInCents(),
                menuItem.getDineInOnly(),
                menuItem.getImagePath(),
                menuItem.getCreatedAt(),
                menuItem.getUpdatedAt()
        ));
    }

    @PutMapping("/{menuItemId}")
    public ResponseEntity<MenuItemResponse> update(
            @PathVariable Long restaurantId, @PathVariable Long menuItemId,
            @Valid @RequestBody UpdateMenuItemRequest request) {
        MenuItem menuItem = new MenuItem(
                restaurantId,
                request.name(),
                request.description(),
                request.priceInCents(),
                request.dineInOnly(),
                request.imagePath()
        );

        MenuItem updatedMenuItem = menuItemUseCase.update(userId, menuItem);

        MenuItemResponse response = new MenuItemResponse(
                updatedMenuItem.getId(),
                updatedMenuItem.getRestaurantId(),
                updatedMenuItem.getName(),
                updatedMenuItem.getDescription(),
                updatedMenuItem.getPriceInCents(),
                updatedMenuItem.getDineInOnly(),
                updatedMenuItem.getImagePath(),
                updatedMenuItem.getCreatedAt(),
                updatedMenuItem.getUpdatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{menuItemId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long restaurantId, @PathVariable Long menuItemId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
