package com.api_application.phase_2_data_guardian.services;

import com.api_application.phase_2_data_guardian.application.model.auth.AuthenticatedUser;
import com.api_application.phase_2_data_guardian.application.model.auth.RegisterCommand;
import com.api_application.phase_2_data_guardian.application.model.auth.TokenData;
import com.api_application.phase_2_data_guardian.application.repository.*;
import com.api_application.phase_2_data_guardian.application.security.*;
import com.api_application.phase_2_data_guardian.domain.entity.*;
import com.api_application.phase_2_data_guardian.domain.exception.*;
import com.api_application.phase_2_data_guardian.domain.services.*;
import java.util.Optional;
import com.api_application.phase_2_data_guardian.application.model.auth.LoginCommand;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    final UserRepository users = mock(UserRepository.class);
    final UserTypeRepository types = mock(UserTypeRepository.class);
    final PasswordHasher hasher = mock(PasswordHasher.class);
    final TokenProvider tokens = mock(TokenProvider.class);

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

    @Test void registerUser() {
        var service = new AuthService(users, types, hasher, tokens);

        RegisterCommand command = new RegisterCommand(
                "John Doe",
                "johndoe@email.com",
                "John@DoeStrongPass#@"
        );

        when(hasher.hash("John@DoeStrongPass#@")).thenReturn("HASHED_PASSWORD");
        when(types.findByNameIgnoreCase("CUSTOMER")).thenReturn(Optional.of(type(1L, "CUSTOMER")));
        when(users.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });
        when(tokens.generate(any(AuthenticatedUser.class)))
                .thenReturn(new TokenData("fake-token", 3600L));

        var result = service.register(command);

        assertEquals("John Doe", result.user().name());
        assertEquals("johndoe@email.com", result.user().email());
        Mockito.verify(hasher).hash(Mockito.eq("John@DoeStrongPass#@"));
        assertEquals("fake-token", result.accessToken());
        assertEquals(3600L, result.expiresIn());
    }

    @Test void registerUserSameEmail() {
        User registeredUser = owner();
        when(users.findByEmailIgnoreCase(registeredUser.getEmail())).thenReturn(Optional.of(registeredUser));

        RegisterCommand command = new RegisterCommand(
                registeredUser.getName(),
                registeredUser.getEmail(),
                registeredUser.getPassword()
        );

        var service = new AuthService(users, types, hasher, tokens);

        assertThrows(ConflictException.class, () -> service.register(command));
        verify(users,never()).save(any(User.class));
        verifyNoInteractions(hasher,tokens);
    }
    private AuthService service() {
        return new AuthService(users,types,hasher,tokens);
    }

    private void prepareRegistration() {
        when(types.findByNameIgnoreCase("CUSTOMER")).thenReturn(Optional.of(type(3L,"CUSTOMER")));
        when(hasher.hash(anyString())).thenReturn("encoded-password");
        when(users.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });
        when(tokens.generate(any(AuthenticatedUser.class))).thenReturn(new TokenData("generated-token",3600L));
    }

    private User prepareLogin() {
        User user = new User("Owner","owner@example.com","stored-hash",2L);
        user.setId(7L);
        when(users.findByEmailIgnoreCase("owner@example.com")).thenReturn(Optional.of(user));
        when(types.findById(2L)).thenReturn(Optional.of(type(2L,"RESTAURANT_OWNER")));
        when(hasher.matches("Correct@123","stored-hash")).thenReturn(true);
        when(tokens.generate(any(AuthenticatedUser.class))).thenReturn(new TokenData("login-token",1800L));
        return user;
    }

    @Test void registrationNormalizesNameAndEmailAndStoresOnlyEncodedPassword() {
        prepareRegistration();
        var result = service().register(new RegisterCommand("  John Doe  ","  JOHN@EXAMPLE.COM  ","Strong@123"));
        var saved = ArgumentCaptor.forClass(User.class);
        verify(users).save(saved.capture());
        assertEquals("John Doe",saved.getValue().getName());
        assertEquals("john@example.com",saved.getValue().getEmail());
        assertEquals("encoded-password",saved.getValue().getPassword());
        assertEquals(3L,saved.getValue().getUserTypeId());
        assertEquals(42L,result.user().id());
        assertEquals(3L,result.user().userTypeId());
        assertEquals("CUSTOMER",result.user().userTypeName());
        assertEquals("Bearer",result.tokenType());
        verify(users).findByEmailIgnoreCase("john@example.com");
        verify(tokens).generate(result.user());
    }

    @Test void duplicateEmailIsDetectedAfterNormalization() {
        User existing = new User("Existing","used@example.com","stored-hash",3L);
        when(users.findByEmailIgnoreCase("used@example.com")).thenReturn(Optional.of(existing));
        assertThrows(ConflictException.class,() -> service().register(
                new RegisterCommand("New name"," USED@EXAMPLE.COM ","Strong@123")));
        verify(users,never()).save(any());
        verifyNoInteractions(types,hasher,tokens);
    }

    @Test void missingDefaultTypeStopsRegistrationBeforeHashingOrSaving() {
        var error = assertThrows(IllegalStateException.class,() -> service().register(
                new RegisterCommand("John","john@example.com","Strong@123")));
        assertTrue(error.getMessage().contains("CUSTOMER"));
        verify(users,never()).save(any());
        verifyNoInteractions(hasher,tokens);
    }

    @Test void registrationDoesNotIssueTokenWhenSavingFails() {
        when(types.findByNameIgnoreCase("CUSTOMER")).thenReturn(Optional.of(type(3L,"CUSTOMER")));
        when(hasher.hash("Strong@123")).thenReturn("encoded");
        when(users.save(any())).thenThrow(new IllegalStateException("Database unavailable"));
        assertThrows(IllegalStateException.class,() -> service().register(
                new RegisterCommand("John","john@example.com","Strong@123")));
        verifyNoInteractions(tokens);
    }

    @Test void loginReturnsStoredUserAndAssociatedTypeRatherThanUserIdAndNameAsType() {
        prepareLogin();
        var result = service().login(new LoginCommand("owner@example.com","Correct@123"));
        assertEquals(7L,result.user().id());
        assertEquals("Owner",result.user().name());
        assertEquals("owner@example.com",result.user().email());
        assertEquals(2L,result.user().userTypeId());
        assertEquals("RESTAURANT_OWNER",result.user().userTypeName());
        assertEquals("login-token",result.accessToken());
        assertEquals("Bearer",result.tokenType());
        assertEquals(1800L,result.expiresIn());
        verify(tokens).generate(result.user());
        verify(users,never()).save(any());
        verify(hasher,never()).hash(anyString());
    }

    @Test void loginNormalizesEmailButPreservesPasswordExactly() {
        prepareLogin();
        service().login(new LoginCommand("  OWNER@EXAMPLE.COM  ","Correct@123"));
        verify(users).findByEmailIgnoreCase("owner@example.com");
        verify(hasher).matches("Correct@123","stored-hash");
    }

    @Test void loginWithUnknownEmailDoesNotIssueToken() {
        var error = assertThrows(InvalidCredentialsException.class,
                () -> service().login(new LoginCommand("unknown@example.com","Correct@123")));
        assertEquals("Email or password invalid.",error.getMessage());
        verifyNoInteractions(types,hasher,tokens);
        verify(users,never()).save(any());
    }

    @Test void loginWithWrongPasswordDoesNotQueryTypeOrIssueToken() {
        User user = new User("Owner","owner@example.com","stored-hash",2L);
        when(users.findByEmailIgnoreCase("owner@example.com")).thenReturn(Optional.of(user));
        var error = assertThrows(InvalidCredentialsException.class,
                () -> service().login(new LoginCommand("owner@example.com","Wrong@123")));
        assertEquals("Email or password invalid.",error.getMessage());
        verify(hasher).matches("Wrong@123","stored-hash");
        verifyNoInteractions(types,tokens);
        verify(users,never()).save(any());
    }

    @Test void loginWithMissingAssociatedTypeDoesNotIssueToken() {
        prepareLogin();
        when(types.findById(2L)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class,
                () -> service().login(new LoginCommand("owner@example.com","Correct@123")));
        verify(tokens,never()).generate(any());
    }

    @Test void loginUsesCurrentStoredTypeAfterTypeChange() {
        User user = prepareLogin();
        user.setUserTypeId(3L);
        when(types.findById(3L)).thenReturn(Optional.of(type(3L,"CUSTOMER")));
        var result = service().login(new LoginCommand("owner@example.com","Correct@123"));
        assertEquals(3L,result.user().userTypeId());
        assertEquals("CUSTOMER",result.user().userTypeName());
        verify(tokens).generate(result.user());
    }

}
