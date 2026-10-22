package com.api_application.phase_2_data_guardian.infra.database.mapper;

import com.api_application.phase_2_data_guardian.domain.entity.OpeningHours;
import com.api_application.phase_2_data_guardian.domain.entity.OpeningPeriod;
import com.api_application.phase_2_data_guardian.infra.database.entity.RestaurantOpeningPeriodJpaEntity;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Component
public class OpeningHoursPersistenceMapper {

    private final OpeningPeriodPersistenceMapper periodMapper;

    public OpeningHoursPersistenceMapper(OpeningPeriodPersistenceMapper periodMapper) {
        this.periodMapper = periodMapper;
    }

    public OpeningHours toDomain(List<RestaurantOpeningPeriodJpaEntity> entities) {
        OpeningHours openingHours = new OpeningHours();

        if (entities == null) {
            return openingHours;
        }

        for (RestaurantOpeningPeriodJpaEntity entity : entities) {
            OpeningPeriod period = periodMapper.toDomain(entity);

            switch (entity.getDayOfWeek()) {
                case MONDAY -> openingHours.getMonday().add(period);
                case TUESDAY -> openingHours.getTuesday().add(period);
                case WEDNESDAY -> openingHours.getWednesday().add(period);
                case THURSDAY -> openingHours.getThursday().add(period);
                case FRIDAY -> openingHours.getFriday().add(period);
                case SATURDAY -> openingHours.getSaturday().add(period);
                case SUNDAY -> openingHours.getSunday().add(period);
            }
        }

        return openingHours;
    }

    public List<RestaurantOpeningPeriodJpaEntity> toJpaEntities(OpeningHours domain) {
        List<RestaurantOpeningPeriodJpaEntity> entities = new ArrayList<>();

        if (domain == null) {
            return entities;
        }

        addPeriods(entities, DayOfWeek.MONDAY, domain.getMonday());
        addPeriods(entities, DayOfWeek.TUESDAY, domain.getTuesday());
        addPeriods(entities, DayOfWeek.WEDNESDAY, domain.getWednesday());
        addPeriods(entities, DayOfWeek.THURSDAY, domain.getThursday());
        addPeriods(entities, DayOfWeek.FRIDAY, domain.getFriday());
        addPeriods(entities, DayOfWeek.SATURDAY, domain.getSaturday());
        addPeriods(entities, DayOfWeek.SUNDAY, domain.getSunday());

        return entities;
    }

    private void addPeriods(
            List<RestaurantOpeningPeriodJpaEntity> entities,
            DayOfWeek dayOfWeek,
            List<OpeningPeriod> periods
    ) {
        if (periods == null) {
            return;
        }

        periods.stream()
                .map(period -> periodMapper.toJpaEntity(dayOfWeek, period))
                .forEach(entities::add);
    }
}
