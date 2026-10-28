package com.api_application.phase_2_data_guardian.services;

import com.api_application.phase_2_data_guardian.application.repository.UserRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.domain.entity.User;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.domain.exception.ConflictException;
import com.api_application.phase_2_data_guardian.domain.exception.ForbiddenOperationException;
import com.api_application.phase_2_data_guardian.domain.services.UserTypeService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserTypeServiceTest {
    final UserRepository users = mock(UserRepository.class);
    final UserTypeRepository types = mock(UserTypeRepository.class);
    final UserTypeService typeService = new UserTypeService(types, users);

    UserType type(Long id, String name) { return new UserType(id, name, null, null); }

    User owner() {
        User user = new User("Owner", "owner@example.com", "hash", 2L);
        user.setId(1L);
        when(users.findById(1L)).thenReturn(Optional.of(user));
        when(types.findById(2L)).thenReturn(Optional.of(type(2L, "RESTAURANT_OWNER")));
        return user;
    }

    @Test void associatedTypeCannotBeDeleted() {
        owner();
        when(types.findById(3L)).thenReturn(Optional.of(type(3L, "CUSTOMER")));
        when(users.existsByUserTypeId(3L)).thenReturn(true);
        assertThrows(ConflictException.class, () -> typeService.delete(1L, 3L));
        verify(types, never()).delete(any());
    }

    @Test void unusedTypeCanBeDeletedByOwner() {
        owner();
        UserType type = type(3L, "OTHER");
        when(types.findById(3L)).thenReturn(Optional.of(type));
        typeService.delete(1L, 3L);
        verify(types).delete(type);
    }

    @Test void customerCannotManageCatalog() {
        owner();
        when(types.findById(2L)).thenReturn(Optional.of(type(2L, "CUSTOMER")));
        assertThrows(ForbiddenOperationException.class, () -> typeService.create(1L, new UserType("OTHER")));
        assertThrows(ForbiddenOperationException.class, () -> typeService.update(1L, 2L, new UserType("OTHER")));
        assertThrows(ForbiddenOperationException.class, () -> typeService.delete(1L, 2L));
        verify(types, never()).save(any());
        verify(types, never()).delete(any());
    }

    @Test void cannotRenameAssociatedType() {
        owner();
        when(users.existsByUserTypeId(2L)).thenReturn(true);
        assertThrows(ConflictException.class, () -> typeService.update(1L, 2L, new UserType("CUSTOMER")));
        verify(types, never()).save(any());
    }
}
