package com.api_application.phase_2_data_guardian.integration;

import com.api_application.phase_2_data_guardian.domain.entity.OpeningPeriod;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.domain.exception.ConflictException;
import com.api_application.phase_2_data_guardian.support.PersistenceIntegrationSupport;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import static org.assertj.core.api.Assertions.*;

class RestaurantPersistenceIntegrationTest extends PersistenceIntegrationSupport {


    @Test void restaurantCrudPersistsAddressAndOpeningPeriods() {
        Restaurant restaurant = restaurant("Original");
        Long periodId = restaurantJpa.findById(restaurant.getId()).orElseThrow().getOpeningPeriods().get(0).getId();
        reload();
        Restaurant loaded = restaurantService.findById(restaurant.getId());
        assertThat(loaded.getAddress().getZipCode()).isEqualTo("01001-000");
        assertThat(loaded.getOpeningHours().getMonday()).hasSize(1);
        assertThat(loaded.getCreatedAt()).isNotNull();
        loaded.setName("Updated");
        restaurantService.update(owner.getId(),loaded.getId(),loaded);
        reload();
        assertThat(restaurantService.findById(loaded.getId()).getName()).isEqualTo("Updated");
        assertThat(restaurantService.findById(loaded.getId()).getOpeningHours().getMonday()).hasSize(1);
        assertThat(restaurantJpa.findById(loaded.getId()).orElseThrow().getOpeningPeriods().get(0).getId()).isEqualTo(periodId);
        restaurantService.delete(owner.getId(),loaded.getId());
        reload();
        assertThat(restaurants.findById(loaded.getId())).isEmpty();
        assertThat(em.createQuery("select count(p) from RestaurantOpeningPeriodJpaEntity p",Long.class)
                .getSingleResult()).isZero();
    }

    @Test void updatingHoursRemovesOrphansAndDoesNotAccumulatePeriods() {
        Restaurant restaurant = restaurant("Hours");
        reload();
        Restaurant data = restaurantService.findById(restaurant.getId());
        data.getOpeningHours().setMonday(List.of());
        data.getOpeningHours().setSunday(List.of(new OpeningPeriod(LocalTime.of(10,0),LocalTime.of(14,0))));
        restaurantService.update(owner.getId(),data.getId(),data);
        reload();
        Restaurant loaded = restaurantService.findById(data.getId());
        assertThat(loaded.getOpeningHours().getMonday()).isEmpty();
        assertThat(loaded.getOpeningHours().getSunday()).hasSize(1);
        assertThat(em.createQuery("select count(p) from RestaurantOpeningPeriodJpaEntity p",Long.class)
                .getSingleResult()).isEqualTo(1L);
    }

    @Test void restaurantPagesPreserveMetadataAndSorting() {
        restaurant("Zeta"); restaurant("Alpha");
        reload();
        var page = restaurantService.findAll(PageRequest.of(0,1,Sort.by("name")));
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(Restaurant::getName).containsExactly("Alpha");
    }

    @Test void restaurantWithMenuIsProtectedByServiceAndForeignKey() {
        Restaurant restaurant = restaurant("Protected");
        item(restaurant.getId(),"Meal");
        reload();
        assertThat(items.existsByRestaurantId(restaurant.getId())).isTrue();
        assertThatThrownBy(() -> restaurantService.delete(owner.getId(),restaurant.getId()))
                .isInstanceOf(ConflictException.class);
        assertThatThrownBy(() -> { restaurantJpa.deleteById(restaurant.getId()); restaurantJpa.flush(); })
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
