package com.api_application.phase_2_data_guardian.mappers;

import com.api_application.phase_2_data_guardian.domain.entity.Address;
import com.api_application.phase_2_data_guardian.domain.entity.OpeningHours;
import com.api_application.phase_2_data_guardian.domain.entity.OpeningPeriod;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.infra.database.entity.RestaurantJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.entity.UserJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.mapper.AddressPersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.mapper.OpeningHoursPersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.mapper.OpeningPeriodPersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.mapper.RestaurantPersistenceMapper;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class RestaurantPersistenceMapperTest {
    final AddressPersistenceMapper addresses = new AddressPersistenceMapper();
    final OpeningPeriodPersistenceMapper periods = new OpeningPeriodPersistenceMapper();
    final OpeningHoursPersistenceMapper hours = new OpeningHoursPersistenceMapper(periods);
    final RestaurantPersistenceMapper restaurants = new RestaurantPersistenceMapper(addresses, hours);
    final OffsetDateTime created = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    final OffsetDateTime updated = created.plusDays(1);

    @Test void restaurantMappingPreservesOwnerAddressHoursIdsAndTimestamps() {
        OpeningHours openingHours = new OpeningHours();
        openingHours.setFriday(List.of(new OpeningPeriod(LocalTime.of(9,0), LocalTime.of(17,0))));
        Restaurant source = new Restaurant(10L, "Restaurant",
                new Address("Street", "42", "Shop", "Center", "City", "SP", "01001-000"),
                "Brazilian", openingHours, 7L, created, updated);
        UserJpaEntity owner = new UserJpaEntity();
        owner.setId(7L);
        RestaurantJpaEntity entity = restaurants.toJpaEntity(source, owner);
        assertThat(entity.getOpeningPeriods()).hasSize(1);
        assertThat(entity.getOpeningPeriods().get(0).getRestaurant()).isSameAs(entity);
        assertThat(restaurants.toDomain(entity)).usingRecursiveComparison().isEqualTo(source);
    }
}
