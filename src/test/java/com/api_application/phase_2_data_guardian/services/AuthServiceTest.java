package com.api_application.phase_2_data_guardian.services;

import com.api_application.phase_2_data_guardian.application.repository.*;
import com.api_application.phase_2_data_guardian.application.security.*;
import com.api_application.phase_2_data_guardian.domain.entity.*;
import com.api_application.phase_2_data_guardian.domain.exception.*;
import com.api_application.phase_2_data_guardian.domain.services.*;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.PageRequest;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    final UserRepository users = mock(UserRepository.class);
    final UserTypeRepository types = mock(UserTypeRepository.class);

    UserType type(Long id, String name) { return new UserType(id, name, null, null); }

    User owner() {
        User user = new User("Owner", "owner@example.com", "hash", 2L);
        user.setId(1L);
        when(users.findById(1L)).thenReturn(Optional.of(user));
        when(types.findById(2L)).thenReturn(Optional.of(type(2L, "RESTAURANT_OWNER")));
        return user;
    }

    @Test void authenticatedUserIncludesStoredType() {
        owner();
        var service = new AuthService(users, types, mock(PasswordHasher.class), mock(TokenProvider.class));
        var result = service.findAuthenticatedUser(1L);
        assertEquals(1L, result.id());
        assertEquals("Owner", result.name());
        assertEquals("owner@example.com", result.email());
        assertEquals(2L, result.userTypeId());
        assertEquals("RESTAURANT_OWNER", result.userTypeName());
    }

    @Test void authenticatedUserMissingUser() {
        var service = new AuthService(users, types, mock(PasswordHasher.class), mock(TokenProvider.class));
        assertThrows(ResourceNotFoundException.class, () -> service.findAuthenticatedUser(99L));
    }

    @Test void authenticatedUserMissingType() {
        owner();
        when(types.findById(2L)).thenReturn(Optional.empty());
        var service = new AuthService(users, types, mock(PasswordHasher.class), mock(TokenProvider.class));
        assertThrows(ResourceNotFoundException.class, () -> service.findAuthenticatedUser(1L));
    }
}
