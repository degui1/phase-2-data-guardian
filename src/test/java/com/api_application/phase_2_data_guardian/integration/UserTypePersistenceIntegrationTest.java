package com.api_application.phase_2_data_guardian.integration;

import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.domain.exception.ConflictException;
import com.api_application.phase_2_data_guardian.support.PersistenceIntegrationSupport;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import static org.assertj.core.api.Assertions.*;

class UserTypePersistenceIntegrationTest extends PersistenceIntegrationSupport {


    @Test void typeCrudNormalizesAndDetectsDuplicateNames() {
        UserType type = typeService.create(owner.getId(), new UserType("  delivery  "));
        assertThat(type.getName()).isEqualTo("DELIVERY");
        assertThatThrownBy(() -> typeService.create(owner.getId(),new UserType("Delivery")))
                .isInstanceOf(ConflictException.class);
        typeService.update(owner.getId(),type.getId(),new UserType("PARTNER"));
        reload();
        assertThat(types.findById(type.getId()).orElseThrow().getName()).isEqualTo("PARTNER");
        typeService.delete(owner.getId(),type.getId());
        reload();
        assertThat(types.findById(type.getId())).isEmpty();
    }

    @Test void associatedTypeDeletionIsBlockedByService() {
        assertThat(users.existsByUserTypeId(ownerType.getId())).isTrue();
        assertThat(users.existsByUserTypeId(customerType.getId())).isFalse();
        assertThatThrownBy(() -> typeService.delete(owner.getId(),ownerType.getId()))
                .isInstanceOf(ConflictException.class);
    }

    @Test void foreignKeyProtectsAssociatedTypeEvenWithoutService() {
        reload();
        assertThatThrownBy(() -> { typeJpa.deleteById(ownerType.getId()); typeJpa.flush(); })
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test void typePagesPreserveMetadataAndSorting() {
        assertThat(typeService.findAll(PageRequest.of(0,1,Sort.by("name"))).getContent())
                .extracting(UserType::getName).containsExactly("CUSTOMER");
    }
}
