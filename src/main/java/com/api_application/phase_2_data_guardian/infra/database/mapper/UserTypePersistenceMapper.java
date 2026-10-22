package com.api_application.phase_2_data_guardian.infra.database.mapper;

import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.infra.database.entity.UserTypeJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserTypePersistenceMapper {

    public UserType toDomain (UserTypeJpaEntity userTypeJpaEntity) {
        return new UserType(
                userTypeJpaEntity.getId(),
                userTypeJpaEntity.getName(),
                userTypeJpaEntity.getCreatedAt(),
                userTypeJpaEntity.getUpdatedAt()
        );
    }

    public UserTypeJpaEntity toJpaEntity (UserType userType) {
        return new UserTypeJpaEntity(
                userType.getId(),
                userType.getName(),
                userType.getCreatedAt(),
                userType.getUpdatedAt()
        );
    }
}
