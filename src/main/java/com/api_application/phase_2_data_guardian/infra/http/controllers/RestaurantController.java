package com.api_application.phase_2_data_guardian.infra.http.controllers;

import com.api_application.phase_2_data_guardian.infra.http.dto.request.restaurant.CreateRestaurantRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.restaurant.UpdateRestaurantRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.RestaurantResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    @PostMapping
    public ResponseEntity<RestaurantResponse> create(@Valid @RequestBody CreateRestaurantRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantResponse>> findAll(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> findById(@PathVariable Long restaurantId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PutMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> update(
            @PathVariable Long restaurantId, @Valid @RequestBody UpdateRestaurantRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Void> delete(@PathVariable Long restaurantId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
