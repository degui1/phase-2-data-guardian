package com.api_application.phase_2_data_guardian.services;

import com.api_application.phase_2_data_guardian.application.model.user.UpdateUserCommand;
import com.api_application.phase_2_data_guardian.domain.exception.BusinessRuleException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.time.OffsetDateTime;
import java.util.List;
import com.api_application.phase_2_data_guardian.application.repository.RestaurantRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.domain.entity.User;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.domain.exception.ConflictException;
import com.api_application.phase_2_data_guardian.domain.exception.ForbiddenOperationException;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import com.api_application.phase_2_data_guardian.domain.services.UserService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    final UserRepository users = mock(UserRepository.class);
    final UserTypeRepository types = mock(UserTypeRepository.class);
    final RestaurantRepository restaurants = mock(RestaurantRepository.class);
    final UserService userService = new UserService(users, types, restaurants);

    UserType type(Long id, String name) { return new UserType(id, name, null, null); }

    User owner() {
        User user = new User("Owner", "owner@example.com", "hash", 2L);
        user.setId(1L);
        when(users.findById(1L)).thenReturn(Optional.of(user));
        when(types.findById(2L)).thenReturn(Optional.of(type(2L, "RESTAURANT_OWNER")));
        return user;
    }

    @Test void cannotChangeAnotherUsersType() {
        assertThrows(ForbiddenOperationException.class, () -> userService.assignUserType(1L, 9L, 2L));
        verifyNoInteractions(users);
    }

    @Test void canChooseOwnerForOwnAccount() {
        owner();
        userService.assignUserType(1L, 1L, 2L);
        verify(users).save(any());
    }

    @Test void cannotSelfAssignCustomType() {
        owner();
        when(types.findById(3L)).thenReturn(Optional.of(type(3L, "CUSTOM")));
        assertThrows(ForbiddenOperationException.class, () -> userService.assignUserType(1L, 1L, 3L));
        verify(users, never()).save(any());
    }

    @Test void ownerWithRestaurantCannotBecomeCustomer() {
        owner();
        when(types.findById(3L)).thenReturn(Optional.of(type(3L, "CUSTOMER")));
        when(restaurants.existsByOwnerId(1L)).thenReturn(true);
        assertThrows(ConflictException.class, () -> userService.assignUserType(1L, 1L, 3L));
        verify(users, never()).save(any());
    }

    @Test void ownerWithoutRestaurantCanBecomeCustomer() {
        owner();
        when(types.findById(3L)).thenReturn(Optional.of(type(3L, "CUSTOMER")));
        userService.assignUserType(1L, 1L, 3L);
        verify(users).save(argThat(user -> user.getUserTypeId().equals(3L)));
    }

    @Test void missingTargetUserCannotBeAssignedType() {
        assertThatThrownBy(() -> userService.assignUserType(7L,7L,2L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(users,never()).save(any());
    }

    @Test void missingTypeCannotBeAssignedToExistingUser() {
        User user = new User("User","user@example.com","test-hash",1L);
        user.setId(7L);
        when(users.findById(7L)).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> userService.assignUserType(7L,7L,2L))
                .isInstanceOf(ResourceNotFoundException.class);
        assertThat(user.getUserTypeId()).isEqualTo(1L);
        verify(users,never()).save(any());
    }
    @Test void listPreservesPaginationFromRepository() {
        var pageable = PageRequest.of(1,1);
        var page = new PageImpl<>(List.of(owner()),pageable,3);
        when(users.findAll(pageable)).thenReturn(page);
        assertThat(userService.findAll(pageable)).isSameAs(page);
    }

    @Test void userCanReadOwnProfile() {
        User user = owner();
        assertThat(userService.findById(1L,1L)).isSameAs(user);
    }

    @Test void otherAccountCannotBeReadUpdatedOrDeleted() {
        assertThatThrownBy(() -> userService.findById(9L,1L)).isInstanceOf(ForbiddenOperationException.class);
        assertThatThrownBy(() -> userService.update(9L,1L,new UpdateUserCommand("Name","email@example.com")))
                .isInstanceOf(ForbiddenOperationException.class);
        assertThatThrownBy(() -> userService.delete(9L,1L)).isInstanceOf(ForbiddenOperationException.class);
        verifyNoInteractions(users,restaurants);
    }

    @Test void missingOwnAccountReturnsNotFoundForAllOperations() {
        assertThatThrownBy(() -> userService.findById(1L,1L)).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> userService.update(1L,1L,new UpdateUserCommand("Name","email@example.com")))
                .isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> userService.delete(1L,1L)).isInstanceOf(ResourceNotFoundException.class);
        verify(users,never()).save(any());
        verify(users,never()).delete(any());
    }

    @Test void updateNormalizesEmailAndPreservesIdentityPasswordTypeAndCreationDate() {
        User user = owner();
        var created = OffsetDateTime.parse("2026-10-01T10:00:00Z");
        user.setCreatedAt(created);
        when(users.save(user)).thenReturn(user);
        User result = userService.update(1L,1L,new UpdateUserCommand("  Updated name  ","  UPDATED@EXAMPLE.COM  "));
        assertThat(result.getName()).isEqualTo("Updated name");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserTypeId()).isEqualTo(2L);
        assertThat(result.getPassword()).isEqualTo("hash");
        assertThat(result.getCreatedAt()).isEqualTo(created);
        verify(users).findByEmailIgnoreCase("updated@example.com");
    }

    @Test void updateRejectsAnotherAccountsEmailBeforeChangingProfile() {
        User user = owner();
        User another = new User("Another","taken@example.com","hash",2L);
        another.setId(2L);
        when(users.findByEmailIgnoreCase("taken@example.com")).thenReturn(Optional.of(another));
        assertThatThrownBy(() -> userService.update(1L,1L,new UpdateUserCommand("Changed","TAKEN@example.com")))
                .isInstanceOf(ConflictException.class);
        assertThat(user.getName()).isEqualTo("Owner");
        assertThat(user.getEmail()).isEqualTo("owner@example.com");
        verify(users,never()).save(any());
    }

    @Test void updateAllowsKeepingOwnEmail() {
        User user = owner();
        when(users.findByEmailIgnoreCase(user.getEmail())).thenReturn(Optional.of(user));
        when(users.save(user)).thenReturn(user);
        assertThat(userService.update(1L,1L,new UpdateUserCommand("Updated","OWNER@example.com")).getEmail())
                .isEqualTo("owner@example.com");
        verify(users).save(user);
    }

    @Test void updateRejectsMissingNameOrEmail() {
        owner();
        for (var command : List.of(new UpdateUserCommand(" ","email@example.com"),
                new UpdateUserCommand("Name",null),new UpdateUserCommand(null,"email@example.com"))) {
            assertThatThrownBy(() -> userService.update(1L,1L,command)).isInstanceOf(BusinessRuleException.class);
        }
        assertThatThrownBy(() -> userService.update(1L,1L,null)).isInstanceOf(BusinessRuleException.class);
        verify(users,never()).save(any());
    }

    @Test void accountWithRestaurantsCannotBeDeleted() {
        owner();
        when(restaurants.existsByOwnerId(1L)).thenReturn(true);
        assertThatThrownBy(() -> userService.delete(1L,1L)).isInstanceOf(ConflictException.class);
        verify(users,never()).delete(any());
    }

    @Test void ownAccountWithoutRestaurantsCanBeDeleted() {
        User user = owner();
        userService.delete(1L,1L);
        verify(users).delete(user);
    }

    @Test void directoryRejectsSortingByPrivateFields() {
        for (String field : List.of("email","passwordHash","password")) {
            var pageable = PageRequest.of(0,10,org.springframework.data.domain.Sort.by(field));
            assertThatThrownBy(() -> userService.findAll(pageable)).isInstanceOf(BusinessRuleException.class);
        }
        verify(users,never()).findAll(any());
    }
}
