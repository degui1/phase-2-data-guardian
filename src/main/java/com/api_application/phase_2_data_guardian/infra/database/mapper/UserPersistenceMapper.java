package com.api_application.phase_2_data_guardian.infra.database.mapper;

import com.api_application.phase_2_data_guardian.domain.entity.User;
import com.api_application.phase_2_data_guardian.infra.database.entity.UserJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.entity.UserTypeJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserJpaEntity userJpaEntity) {
        return new User(
                userJpaEntity.getId(),
                userJpaEntity.getName(),
                userJpaEntity.getEmail(),
                userJpaEntity.getPasswordHash(),
                userJpaEntity.getUserType().getId(),
                userJpaEntity.getCreatedAt(),
                userJpaEntity.getUpdatedAt()
        );
    }

    public UserJpaEntity toJpaEntity(User user, UserTypeJpaEntity userTypeJpaEntity) {
        return new UserJpaEntity(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                userTypeJpaEntity,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
