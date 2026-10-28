package com.api_application.phase_2_data_guardian.mappers;

import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.infra.database.entity.RestaurantJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.mapper.MenuItemPersistenceMapper;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MenuItemPersistenceMapperTest {
    final OffsetDateTime created = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    final OffsetDateTime updated = created.plusDays(1);

    @Test void menuMappingPreservesLongPriceWithoutRoundingAndAllFields() {
        RestaurantJpaEntity restaurant = new RestaurantJpaEntity(10L, "Restaurant", null, "Brazilian",
                null, new ArrayList<>(), created, updated);
        MenuItem source = new MenuItem(11L, 10L, "Meal", "Description",
                9007199254740993L, true, "/meal.jpg", created, updated);
        MenuItemPersistenceMapper mapper = new MenuItemPersistenceMapper();
        assertThat(mapper.toDomain(mapper.toJpaEntity(source, restaurant)))
                .usingRecursiveComparison().isEqualTo(source);
    }
}
