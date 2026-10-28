package com.api_application.phase_2_data_guardian.integration;

import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import com.api_application.phase_2_data_guardian.support.PersistenceIntegrationSupport;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import static org.assertj.core.api.Assertions.*;

class MenuItemPersistenceIntegrationTest extends PersistenceIntegrationSupport {


    @Test void menuCrudKeepsRestaurantLinkAndPersistsCents() {
        Restaurant restaurant = restaurant("Menu");
        MenuItem item = item(restaurant.getId(),"Meal");
        reload();
        MenuItem loaded = menuService.findById(restaurant.getId(),item.getId());
        assertThat(loaded.getPriceInCents()).isEqualTo(2590L);
        loaded.setPriceInCents(2890L);
        loaded.setName("Updated meal");
        menuService.update(owner.getId(),restaurant.getId(),item.getId(),loaded);
        reload();
        assertThat(menuService.findById(restaurant.getId(),item.getId()).getPriceInCents()).isEqualTo(2890L);
        menuService.delete(owner.getId(),restaurant.getId(),item.getId());
        reload();
        assertThat(items.findByIdAndRestaurantId(item.getId(),restaurant.getId())).isEmpty();
    }

    @Test void wrongRestaurantIsRejectedByAllItemOperations() {
        Restaurant first = restaurant("First");
        Restaurant second = restaurant("Second");
        MenuItem item = item(first.getId(),"Meal");
        reload();
        assertThat(items.findByIdAndRestaurantId(item.getId(),second.getId())).isEmpty();
        assertThatThrownBy(() -> menuService.findById(second.getId(),item.getId())).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> menuService.update(owner.getId(),second.getId(),item.getId(),item)).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> menuService.delete(owner.getId(),second.getId(),item.getId())).isInstanceOf(ResourceNotFoundException.class);
        assertThat(items.findByIdAndRestaurantId(item.getId(),first.getId())).isPresent();
    }

    @Test void menuPaginationAndOrderingAreScopedToRestaurant() {
        Restaurant first = restaurant("First");
        Restaurant second = restaurant("Second");
        item(first.getId(),"Zucchini");
        item(first.getId(),"Apple");
        item(first.getId(),"Mango");
        item(second.getId(),"Banana");
        reload();
        var page = menuService.findByRestaurantId(first.getId(),PageRequest.of(1,1,Sort.by("name")));
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getContent()).extracting(MenuItem::getName).containsExactly("Mango");
        assertThat(page.getContent()).allMatch(i -> i.getRestaurantId().equals(first.getId()));
    }

    @Test void duplicateMenuNamesWithinRestaurantAreRejectedByDatabase() {
        Restaurant restaurant = restaurant("Duplicates");
        item(restaurant.getId(),"Meal");
        assertThatThrownBy(() -> { item(restaurant.getId(),"Meal"); em.flush(); })
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
