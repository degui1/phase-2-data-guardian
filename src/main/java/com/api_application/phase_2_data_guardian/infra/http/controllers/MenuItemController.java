package com.api_application.phase_2_data_guardian.infra.http.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

import com.api_application.phase_2_data_guardian.application.usecase.MenuItemUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.menuitem.CreateMenuItemRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.menuitem.UpdateMenuItemRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.MenuItemResponse;
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

@Tag(name = "Cardápio")
@RestController
@RequestMapping("/restaurants/{restaurantId}/menu-items")
public class MenuItemController {

    private final MenuItemUseCase menuItemUseCase;

    public MenuItemController(MenuItemUseCase menuItemUseCase) {
        this.menuItemUseCase = menuItemUseCase;
    }

    @PostMapping
    @Operation(summary = "Criar item do cardápio", description = "Somente o proprietário. priceInCents é Long em centavos, maior ou igual a zero.")
    @ApiResponse(responseCode = "201", description = "Recurso criado; URL no header Location")
    public ResponseEntity<MenuItemResponse> create(
            @PathVariable Long restaurantId,
            @Parameter(hidden = true) Authentication authentication,
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

        MenuItem createdMenuItem = menuItemUseCase.create(authenticatedUserId(authentication), menuItem);

        MenuItemResponse response = new MenuItemResponse(createdMenuItem.getId(), createdMenuItem.getRestaurantId(), createdMenuItem.getName(), createdMenuItem.getDescription(), createdMenuItem.getPriceInCents(), createdMenuItem.getDineInOnly(), createdMenuItem.getImagePath(), createdMenuItem.getCreatedAt(), createdMenuItem.getUpdatedAt());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{menuItemId}")
                .buildAndExpand(createdMenuItem.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar cardápio do restaurante", description = "Busca paginada restrita ao restaurante informado; page inicia em zero.")
    public ResponseEntity<Page<MenuItemResponse>> findAll(
            @PathVariable Long restaurantId,
            @ParameterObject @PageableDefault(
                    size = 10,
                    sort = "name",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<MenuItemResponse> menuItems = menuItemUseCase
                .findByRestaurantId(restaurantId, pageable)
                .map(item -> new MenuItemResponse(item.getId(), item.getRestaurantId(), item.getName(), item.getDescription(), item.getPriceInCents(), item.getDineInOnly(), item.getImagePath(), item.getCreatedAt(), item.getUpdatedAt()));

        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/{menuItemId}")
    @Operation(summary = "Consultar item do restaurante", description = "O item deve pertencer ao restaurante da URL; caso contrário, retorna 404.")
    public ResponseEntity<MenuItemResponse> findById(
            @PathVariable Long restaurantId, @PathVariable Long menuItemId) {
        MenuItem menuItem = menuItemUseCase.findById(restaurantId, menuItemId);

        return ResponseEntity.ok(new MenuItemResponse(menuItem.getId(), menuItem.getRestaurantId(), menuItem.getName(), menuItem.getDescription(), menuItem.getPriceInCents(), menuItem.getDineInOnly(), menuItem.getImagePath(), menuItem.getCreatedAt(), menuItem.getUpdatedAt()));
    }

    @PutMapping("/{menuItemId}")
    @Operation(summary = "Atualizar item do restaurante", description = "Somente o proprietário. Item e restaurante são consultados juntos; não permite transferir o item.")
    public ResponseEntity<MenuItemResponse> update(
            @PathVariable Long restaurantId, @PathVariable Long menuItemId,
            @Parameter(hidden = true) Authentication authentication,
            @Valid @RequestBody UpdateMenuItemRequest request) {
        MenuItem menuItem = new MenuItem(
                restaurantId,
                request.name(),
                request.description(),
                request.priceInCents(),
                request.dineInOnly(),
                request.imagePath()
        );

        MenuItem updatedMenuItem = menuItemUseCase.update(authenticatedUserId(authentication), restaurantId, menuItemId, menuItem);

        MenuItemResponse response = new MenuItemResponse(updatedMenuItem.getId(), updatedMenuItem.getRestaurantId(), updatedMenuItem.getName(), updatedMenuItem.getDescription(), updatedMenuItem.getPriceInCents(), updatedMenuItem.getDineInOnly(), updatedMenuItem.getImagePath(), updatedMenuItem.getCreatedAt(), updatedMenuItem.getUpdatedAt());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{menuItemId}")
    @Operation(summary = "Excluir item do restaurante", description = "Somente o proprietário. Item fora do restaurante da URL retorna 404.")
    @ApiResponse(responseCode = "204", description = "Recurso excluído", content = @io.swagger.v3.oas.annotations.media.Content)
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true) Authentication authentication,
            @PathVariable Long restaurantId,
            @PathVariable Long menuItemId
    ) {
        menuItemUseCase.delete(authenticatedUserId(authentication), restaurantId, menuItemId);
        return ResponseEntity.noContent().build();
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
