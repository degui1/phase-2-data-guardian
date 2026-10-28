package com.api_application.phase_2_data_guardian.mappers;

import com.api_application.phase_2_data_guardian.domain.entity.Address;
import com.api_application.phase_2_data_guardian.infra.database.mapper.AddressPersistenceMapper;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class AddressPersistenceMapperTest {
    final AddressPersistenceMapper addresses = new AddressPersistenceMapper();

    @Test void addressRoundTripPreservesAllFieldsAndOptionalComplement() {
        Address source = new Address("Street", "42", null, "Center", "City", "SP", "01001-000");
        assertThat(addresses.toDomain(addresses.toJpaEntity(source))).usingRecursiveComparison().isEqualTo(source);
        assertThat(addresses.toDomain(null)).isNull();
        assertThat(addresses.toJpaEntity(null)).isNull();
    }
}
