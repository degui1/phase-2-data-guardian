package com.api_application.phase_2_data_guardian.mappers;

import com.api_application.phase_2_data_guardian.domain.entity.OpeningHours;
import com.api_application.phase_2_data_guardian.domain.entity.OpeningPeriod;
import com.api_application.phase_2_data_guardian.infra.database.mapper.OpeningHoursPersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.mapper.OpeningPeriodPersistenceMapper;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.assertThat;

class OpeningHoursPersistenceMapperTest {
    final OpeningPeriodPersistenceMapper periods = new OpeningPeriodPersistenceMapper();
    final OpeningHoursPersistenceMapper hours = new OpeningHoursPersistenceMapper(periods);

    @ParameterizedTest @EnumSource(DayOfWeek.class)
    void openingPeriodsAreAssignedToTheCorrectDay(DayOfWeek day) {
        var p = periods.toJpaEntity(day, new OpeningPeriod(LocalTime.of(8, 15), LocalTime.of(18, 45)));
        OpeningHours result = hours.toDomain(List.of(p));
        var roundTrip = hours.toJpaEntities(result);
        assertThat(roundTrip).hasSize(1);
        assertThat(roundTrip.get(0).getDayOfWeek()).isEqualTo(day);
        assertThat(roundTrip.get(0).getOpensAt()).isEqualTo(LocalTime.of(8, 15));
        assertThat(roundTrip.get(0).getClosesAt()).isEqualTo(LocalTime.of(18, 45));
    }

    @Test void multiplePeriodsAndClosedDaysSurviveRoundTrip() {
        OpeningHours source = new OpeningHours();
        source.setMonday(List.of(new OpeningPeriod(LocalTime.of(8,0), LocalTime.NOON),
                new OpeningPeriod(LocalTime.of(14,0), LocalTime.of(18,0))));
        source.setSunday(List.of(new OpeningPeriod(LocalTime.of(9,0), LocalTime.of(13,0))));
        assertThat(hours.toDomain(hours.toJpaEntities(source))).usingRecursiveComparison().isEqualTo(source);
        assertThat(hours.toJpaEntities(null)).isEmpty();
        assertThat(hours.toDomain(null)).usingRecursiveComparison().isEqualTo(new OpeningHours());
    }
}
