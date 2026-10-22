package com.api_application.phase_2_data_guardian.infra.database.mapper;

import com.api_application.phase_2_data_guardian.domain.entity.Address;
import com.api_application.phase_2_data_guardian.infra.database.entity.AddressJpaEmbeddable;
import org.springframework.stereotype.Component;

@Component
public class AddressPersistenceMapper {

    public Address toDomain(AddressJpaEmbeddable entity) {
        if (entity == null) {
            return null;
        }

        return new Address(
                entity.getStreet(),
                entity.getNumber(),
                entity.getComplement(),
                entity.getNeighborhood(),
                entity.getCity(),
                entity.getState(),
                entity.getZipCode()
        );
    }

    public AddressJpaEmbeddable toJpaEntity(Address domain) {
        if (domain == null) {
            return null;
        }

        return new AddressJpaEmbeddable(
                domain.getStreet(),
                domain.getNumber(),
                domain.getComplement(),
                domain.getNeighborhood(),
                domain.getCity(),
                domain.getState(),
                domain.getZipCode()
        );
    }
}
