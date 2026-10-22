package com.api_application.phase_2_data_guardian.infra.database.mapper;

import com.api_application.phase_2_data_guardian.domain.entity.OpeningPeriod;
import com.api_application.phase_2_data_guardian.infra.database.entity.RestaurantOpeningPeriodJpaEntity;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class OpeningPeriodPersistenceMapper {

    public OpeningPeriod toDomain(RestaurantOpeningPeriodJpaEntity entity) {
        return new OpeningPeriod(
                entity.getOpensAt(),
                entity.getClosesAt()
        );
    }

    public RestaurantOpeningPeriodJpaEntity toJpaEntity(
            DayOfWeek dayOfWeek,
            OpeningPeriod domain
    ) {
        return new RestaurantOpeningPeriodJpaEntity(
                dayOfWeek,
                domain.getOpensAt(),
                domain.getClosesAt()
        );
    }
}
