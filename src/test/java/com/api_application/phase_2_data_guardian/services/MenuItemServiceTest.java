package com.api_application.phase_2_data_guardian.services;

import com.api_application.phase_2_data_guardian.application.repository.MenuItemRepository;
import com.api_application.phase_2_data_guardian.application.repository.RestaurantRepository;
import com.api_application.phase_2_data_guardian.domain.entity.Address;
import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import com.api_application.phase_2_data_guardian.domain.entity.OpeningHours;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.domain.exception.BusinessRuleException;
import com.api_application.phase_2_data_guardian.domain.exception.ForbiddenOperationException;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import com.api_application.phase_2_data_guardian.domain.services.MenuItemService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.PageRequest;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuItemServiceTest {
    final RestaurantRepository restaurants = mock(RestaurantRepository.class);
    final MenuItemRepository items = mock(MenuItemRepository.class);
    final MenuItemService menuService = new MenuItemService(items, restaurants);

    Restaurant restaurant() {
        Restaurant restaurant = new Restaurant("Restaurant", new Address(), "Brazilian", new OpeningHours(), 1L);
        restaurant.setId(10L);
        when(restaurants.findById(10L)).thenReturn(Optional.of(restaurant));
        return restaurant;
    }

    Restaurant validationRestaurant() {
        Restaurant result = new Restaurant("Restaurant",new Address(),"Brazilian",new OpeningHours(),7L);
        result.setId(10L);
        return result;
    }

    @Test void wrongRestaurantUrlCannotReadUpdateOrDeleteItem() {
        assertThrows(ResourceNotFoundException.class, () -> menuService.findById(99L, 20L));
        assertThrows(ResourceNotFoundException.class, () -> menuService.update(1L, 99L, 20L, new MenuItem()));
        assertThrows(ResourceNotFoundException.class, () -> menuService.delete(1L, 99L, 20L));
        verify(items, times(3)).findByIdAndRestaurantId(20L, 99L);
        verify(items, never()).save(any());
        verify(items, never()).delete(any());
    }

    @Test void menuOwnerCanUpdateAndDeleteWithinRestaurant() {
        restaurant();
        MenuItem item = new MenuItem(10L, "Old", "Description", 100L, false, "photo");
        when(items.findByIdAndRestaurantId(20L, 10L)).thenReturn(Optional.of(item));
        MenuItem data = new MenuItem(99L, "New", "Description", 200L, true, "new-photo");
        menuService.update(1L, 10L, 20L, data);
        assertEquals(10L, item.getRestaurantId());
        assertEquals("New", item.getName());
        verify(items).save(item);
        menuService.delete(1L, 10L, 20L);
        verify(items).delete(item);
    }

    @Test void menuNonOwnerCannotCreateUpdateOrDelete() {
        restaurant();
        MenuItem item = new MenuItem(10L, "Item", "Description", 100L, false, "photo");
        when(items.findByIdAndRestaurantId(20L, 10L)).thenReturn(Optional.of(item));
        assertThrows(ForbiddenOperationException.class, () -> menuService.create(9L, item));
        assertThrows(ForbiddenOperationException.class, () -> menuService.update(9L, 10L, 20L, item));
        assertThrows(ForbiddenOperationException.class, () -> menuService.delete(9L, 10L, 20L));
        verify(items, never()).save(any());
        verify(items, never()).delete(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"name","nullPrice","negativePrice"})
    void invalidMenuItemIsRejectedWithoutWriting(String invalidField) {
        when(restaurants.findById(10L)).thenReturn(Optional.of(validationRestaurant()));
        MenuItem item = new MenuItem(10L,"Meal","Description",100L,false,"/image.jpg");
        switch (invalidField) {
            case "name" -> item.setName(null);
            case "nullPrice" -> item.setPriceInCents(null);
            case "negativePrice" -> item.setPriceInCents(-1L);
            default -> throw new AssertionError(invalidField);
        }
        assertThatThrownBy(() -> menuService.create(7L,item)).isInstanceOf(BusinessRuleException.class);
        verify(items,never()).save(any());
    }

    @Test void missingRestaurantIsNotAnEmptyMenuPage() {
        assertThatThrownBy(() -> menuService.findByRestaurantId(10L,PageRequest.of(0,10)))
                .isInstanceOf(ResourceNotFoundException.class);
        verifyNoInteractions(items);
    }

    @Test void menuAllowsFreeItemWithoutChangingZeroPrice() {
        when(restaurants.findById(10L)).thenReturn(Optional.of(validationRestaurant()));
        MenuItem item = new MenuItem(10L,"Water","Free water",0L,false,"/image.jpg");
        when(items.save(item)).thenReturn(item);
        assertThat(menuService.create(7L,item).getPriceInCents()).isZero();
    }
}
