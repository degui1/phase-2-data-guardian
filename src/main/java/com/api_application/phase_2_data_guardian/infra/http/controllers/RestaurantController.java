package com.api_application.phase_2_data_guardian.infra.http.controllers;

import com.api_application.phase_2_data_guardian.application.usecase.RestaurantUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.Address;
import com.api_application.phase_2_data_guardian.domain.entity.OpeningHours;
import com.api_application.phase_2_data_guardian.domain.entity.OpeningPeriod;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.common.AddressRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.common.OpeningHoursRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.common.OpeningPeriodRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.restaurant.CreateRestaurantRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.request.restaurant.UpdateRestaurantRequest;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.RestaurantResponse;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.common.AddressResponse;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.common.OpeningHoursResponse;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.common.OpeningPeriodResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    private final RestaurantUseCase restaurantUseCase;

    public RestaurantController(RestaurantUseCase restaurantUseCase) {
        this.restaurantUseCase = restaurantUseCase;
    }

    @PostMapping
    public ResponseEntity<RestaurantResponse> create(
            Authentication authentication,
            @Valid @RequestBody CreateRestaurantRequest request
    ) {
        Restaurant restaurant = new Restaurant(
                request.name(),
                toDomain(request.address()),
                request.cuisineType(),
                toDomain(request.openingHours()),
                authenticatedUserId(authentication)
        );
        Restaurant created = restaurantUseCase.create(restaurant);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{restaurantId}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(created));
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantResponse>> findAll(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(restaurantUseCase.findAll(pageable).map(this::toResponse));
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> findById(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(toResponse(restaurantUseCase.findById(restaurantId)));
    }

    @PutMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> update(
            Authentication authentication,
            @PathVariable Long restaurantId,
            @Valid @RequestBody UpdateRestaurantRequest request
    ) {
        Restaurant data = new Restaurant(
                request.name(),
                toDomain(request.address()),
                request.cuisineType(),
                toDomain(request.openingHours()),
                authenticatedUserId(authentication)
        );
        Restaurant updated = restaurantUseCase.update(
                authenticatedUserId(authentication),
                restaurantId,
                data
        );
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long restaurantId) {
        restaurantUseCase.delete(authenticatedUserId(authentication), restaurantId);
        return ResponseEntity.noContent().build();
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }

    private Address toDomain(AddressRequest request) {
        return new Address(
                request.street(), request.number(), request.complement(), request.neighborhood(),
                request.city(), request.state(), request.zipCode()
        );
    }

    private OpeningHours toDomain(OpeningHoursRequest request) {
        return new OpeningHours(
                toDomainPeriods(request.monday()), toDomainPeriods(request.tuesday()),
                toDomainPeriods(request.wednesday()), toDomainPeriods(request.thursday()),
                toDomainPeriods(request.friday()), toDomainPeriods(request.saturday()),
                toDomainPeriods(request.sunday())
        );
    }

    private List<OpeningPeriod> toDomainPeriods(List<OpeningPeriodRequest> periods) {
        return periods.stream().map(period -> new OpeningPeriod(period.opensAt(), period.closesAt())).toList();
    }

    private RestaurantResponse toResponse(Restaurant restaurant) {
        Address address = restaurant.getAddress();
        AddressResponse addressResponse = new AddressResponse(
                address.getStreet(), address.getNumber(), address.getComplement(), address.getNeighborhood(),
                address.getCity(), address.getState(), address.getZipCode()
        );
        OpeningHours hours = restaurant.getOpeningHours();
        OpeningHoursResponse hoursResponse = new OpeningHoursResponse(
                toResponsePeriods(hours.getMonday()), toResponsePeriods(hours.getTuesday()),
                toResponsePeriods(hours.getWednesday()), toResponsePeriods(hours.getThursday()),
                toResponsePeriods(hours.getFriday()), toResponsePeriods(hours.getSaturday()),
                toResponsePeriods(hours.getSunday())
        );
        return new RestaurantResponse(
                restaurant.getId(), restaurant.getName(), addressResponse, restaurant.getCuisineType(),
                hoursResponse, restaurant.getOwnerId(), restaurant.getCreatedAt(), restaurant.getUpdatedAt()
        );
    }

    private List<OpeningPeriodResponse> toResponsePeriods(List<OpeningPeriod> periods) {
        return periods.stream()
                .map(period -> new OpeningPeriodResponse(period.getOpensAt(), period.getClosesAt()))
                .toList();
    }
}
