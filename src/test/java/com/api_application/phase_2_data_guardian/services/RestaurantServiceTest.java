package com.api_application.phase_2_data_guardian.services;

import com.api_application.phase_2_data_guardian.application.repository.MenuItemRepository;
import com.api_application.phase_2_data_guardian.application.repository.RestaurantRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.domain.entity.Address;
import com.api_application.phase_2_data_guardian.domain.entity.OpeningHours;
import com.api_application.phase_2_data_guardian.domain.entity.OpeningPeriod;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.domain.entity.User;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.domain.exception.BusinessRuleException;
import com.api_application.phase_2_data_guardian.domain.exception.ConflictException;
import com.api_application.phase_2_data_guardian.domain.exception.ForbiddenOperationException;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import com.api_application.phase_2_data_guardian.domain.services.RestaurantService;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantServiceTest {
    final UserRepository users = mock(UserRepository.class);
    final UserTypeRepository types = mock(UserTypeRepository.class);
    final RestaurantRepository restaurants = mock(RestaurantRepository.class);
    final MenuItemRepository items = mock(MenuItemRepository.class);
    final RestaurantService restaurantService = new RestaurantService(restaurants, users, types, items);

    UserType type(Long id, String name) { return new UserType(id, name, null, null); }

    User owner() {
        User user = new User("Owner", "owner@example.com", "hash", 2L);
        user.setId(1L);
        when(users.findById(1L)).thenReturn(Optional.of(user));
        when(types.findById(2L)).thenReturn(Optional.of(type(2L, "RESTAURANT_OWNER")));
        return user;
    }

    Restaurant restaurant() {
        Restaurant restaurant = new Restaurant("Restaurant", new Address(), "Brazilian", new OpeningHours(), 1L);
        restaurant.setId(10L);
        when(restaurants.findById(10L)).thenReturn(Optional.of(restaurant));
        return restaurant;
    }

    OpeningPeriod period(String start, String end) {
        return new OpeningPeriod(LocalTime.parse(start), LocalTime.parse(end));
    }

    Restaurant validationRestaurant() {
        Restaurant result = new Restaurant("Restaurant",new Address(),"Brazilian",new OpeningHours(),7L);
        result.setId(10L);
        return result;
    }

    @Test void restaurantRequiresExistingOwner() {
        var restaurant = restaurant();
        assertThrows(ResourceNotFoundException.class, () -> restaurantService.create(restaurant));
        verify(restaurants, never()).save(any());
    }

    @Test void restaurantRequiresOwnerType() {
        owner();
        when(types.findById(2L)).thenReturn(Optional.of(type(2L, "CUSTOMER")));
        var restaurant = restaurant();
        assertThrows(ForbiddenOperationException.class, () -> restaurantService.create(restaurant));
        verify(restaurants, never()).save(any());
    }

    @Test void restaurantAcceptsAdjacentUnsortedPeriods() {
        owner();
        var restaurant = restaurant();
        restaurant.getOpeningHours().setMonday(List.of(period("12:00","18:00"), period("08:00","12:00")));
        restaurantService.create(restaurant);
        verify(restaurants).save(restaurant);
    }

    @Test void restaurantRejectsEqualOrReversedTimes() {
        for (var p : List.of(period("12:00","12:00"), period("18:00","08:00"))) {
            var restaurant = restaurant();
            restaurant.getOpeningHours().setMonday(List.of(p));
            assertThrows(BusinessRuleException.class, () -> restaurantService.create(restaurant));
        }
        verify(restaurants, never()).save(any());
    }

    @Test void restaurantRejectsNestedOrOverlappingPeriods() {
        for (var p : List.of(period("09:00","10:00"), period("11:00","14:00"))) {
            var restaurant = restaurant();
            restaurant.getOpeningHours().setSunday(List.of(period("08:00","12:00"), p));
            assertThrows(BusinessRuleException.class, () -> restaurantService.create(restaurant));
        }
        verify(restaurants, never()).save(any());
    }

    @Test void restaurantUpdateValidatesHours() {
        var existing = restaurant();
        var invalid = restaurant();
        invalid.getOpeningHours().setMonday(List.of(period("18:00","08:00")));
        assertThrows(BusinessRuleException.class, () -> restaurantService.update(1L, 10L, invalid));
        verify(restaurants, never()).save(any());
    }

    @Test void restaurantWithItemsCannotBeDeleted() {
        restaurant();
        when(items.existsByRestaurantId(10L)).thenReturn(true);
        assertThrows(ConflictException.class, () -> restaurantService.delete(1L, 10L));
        verify(restaurants, never()).delete(any());
    }

    @Test void emptyRestaurantCanBeDeleted() {
        var restaurant = restaurant();
        restaurantService.delete(1L, 10L);
        verify(restaurants).delete(restaurant);
    }

    @Test void restaurantNonOwnerCannotUpdateOrDelete() {
        var restaurant = restaurant();
        assertThrows(ForbiddenOperationException.class, () -> restaurantService.update(9L, 10L, restaurant));
        assertThrows(ForbiddenOperationException.class, () -> restaurantService.delete(9L, 10L));
        verify(restaurants, never()).delete(any());
        verify(restaurants, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"name","cuisine","address","hours","owner","nullDay","nullPeriod","opensAt","closesAt"})
    void invalidRestaurantIsRejectedWithoutWriting(String invalidField) {
        Restaurant data = validationRestaurant();
        switch (invalidField) {
            case "name" -> data.setName(" ");
            case "cuisine" -> data.setCuisineType(null);
            case "address" -> data.setAddress(null);
            case "hours" -> data.setOpeningHours(null);
            case "owner" -> data.setOwnerId(null);
            case "nullDay" -> data.getOpeningHours().setMonday(null);
            case "nullPeriod" -> data.getOpeningHours().setMonday(Arrays.asList((OpeningPeriod) null));
            case "opensAt" -> data.getOpeningHours().setMonday(List.of(new OpeningPeriod(null,LocalTime.NOON)));
            case "closesAt" -> data.getOpeningHours().setMonday(List.of(new OpeningPeriod(LocalTime.of(8,0),null)));
            default -> throw new AssertionError(invalidField);
        }
        assertThatThrownBy(() -> restaurantService.create(data)).isInstanceOf(BusinessRuleException.class);
        verify(restaurants,never()).save(any());
    }

    @Test void missingRestaurantOwnerTypeIsRejected() {
        when(users.findById(7L)).thenReturn(Optional.of(new User("Owner","owner@example.com","test-hash",2L)));
        assertThatThrownBy(() -> restaurantService.create(validationRestaurant()))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(restaurants,never()).save(any());
    }
}
