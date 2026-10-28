package com.api_application.phase_2_data_guardian.mappers;

import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.infra.database.mapper.UserTypePersistenceMapper;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserTypePersistenceMapperTest {
    final OffsetDateTime created = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    final OffsetDateTime updated = created.plusDays(1);

    @Test void userTypeMappingPreservesIdentityAndTimestamps() {
        UserType source = new UserType(2L, "RESTAURANT_OWNER", created, updated);
        UserTypePersistenceMapper mapper = new UserTypePersistenceMapper();
        assertThat(mapper.toDomain(mapper.toJpaEntity(source))).usingRecursiveComparison().isEqualTo(source);
    }
}
