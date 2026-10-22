package com.api_application.phase_2_data_guardian.infra.database.mapper;

import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.infra.database.entity.RestaurantJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class RestaurantPersistenceMapper {

    private final AddressPersistenceMapper addressMapper;
    private final OpeningHoursPersistenceMapper openingHoursMapper;

    public RestaurantPersistenceMapper(
            AddressPersistenceMapper addressMapper,
            OpeningHoursPersistenceMapper openingHoursMapper
    ) {
        this.addressMapper = addressMapper;
        this.openingHoursMapper = openingHoursMapper;
    }

    public Restaurant toDomain(RestaurantJpaEntity entity) {
        return new Restaurant(
                entity.getId(),
                entity.getName(),
                addressMapper.toDomain(entity.getAddress()),
                entity.getCuisineType(),
                openingHoursMapper.toDomain(entity.getOpeningPeriods()),
                entity.getOwner().getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public RestaurantJpaEntity toJpaEntity(Restaurant domain, UserJpaEntity owner) {
        RestaurantJpaEntity entity = new RestaurantJpaEntity(
                domain.getId(),
                domain.getName(),
                addressMapper.toJpaEntity(domain.getAddress()),
                domain.getCuisineType(),
                owner,
                new ArrayList<>(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );

        openingHoursMapper.toJpaEntities(domain.getOpeningHours())
                .forEach(entity::addOpeningPeriod);

        return entity;
    }
}
