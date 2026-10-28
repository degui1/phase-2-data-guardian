package com.api_application.phase_2_data_guardian.mappers;

import com.api_application.phase_2_data_guardian.domain.entity.User;
import com.api_application.phase_2_data_guardian.infra.database.entity.UserTypeJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.mapper.UserPersistenceMapper;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserPersistenceMapperTest {
    @Test void roundTripPreservesStoredHashTypeIdentityAndTimestamps() {
        var created=OffsetDateTime.parse("2026-10-01T10:00:00Z");
        User user=new User(7L,"Owner","owner@example.com","encoded-password",2L,created,created.plusDays(1));
        var type=new UserTypeJpaEntity(2L,"RESTAURANT_OWNER",created,created);
        var mapper=new UserPersistenceMapper();
        var entity=mapper.toJpaEntity(user,type);
        assertThat(entity.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(entity.getUserType()).isSameAs(type);
        assertThat(mapper.toDomain(entity)).usingRecursiveComparison().isEqualTo(user);
    }
}
