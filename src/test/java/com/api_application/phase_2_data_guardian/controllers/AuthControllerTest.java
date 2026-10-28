package com.api_application.phase_2_data_guardian.controllers;

import com.api_application.phase_2_data_guardian.application.model.auth.*;
import com.api_application.phase_2_data_guardian.application.usecase.AuthUseCase;
import com.api_application.phase_2_data_guardian.domain.exception.*;
import com.api_application.phase_2_data_guardian.infra.http.controllers.AuthController;
import com.api_application.phase_2_data_guardian.infra.http.exception.CustomControllerAdvice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {
    final AuthUseCase auth = mock(AuthUseCase.class);
    final MockMvc mvc = MockMvcBuilders.standaloneSetup(new AuthController(auth))
            .setControllerAdvice(new CustomControllerAdvice()).build();
    final AuthenticatedUser user = new AuthenticatedUser(7L,"John","john@example.com",3L,"CUSTOMER");
    final AuthenticationResult result = new AuthenticationResult("token","Bearer",3600L,user);
    final String registration = """
            {"name":"John","email":"john@example.com","password":"Strong@123"}
            """;
    final String login = """
            {"email":"john@example.com","password":"Strong@123"}
            """;

    @Test void registerReturns201LocationAndTokenWithoutPassword() throws Exception {
        when(auth.register(new RegisterCommand("John","john@example.com","Strong@123"))).thenReturn(result);
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isCreated()).andExpect(header().string("Location","http://localhost/users/7"))
                .andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.user.userType.id").value(3))
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist());
    }

    @Test void loginReturns200WithNestedUserType() throws Exception {
        when(auth.login(new LoginCommand("john@example.com","Strong@123"))).thenReturn(result);
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(login))
                .andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.user.id").value(7))
                .andExpect(jsonPath("$.user.userType.name").value("CUSTOMER"))
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test void invalidCredentialsReturnGenericProblem401() throws Exception {
        when(auth.login(any())).thenThrow(new InvalidCredentialsException("Private internal detail"));
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(login))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Invalid credentials"))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test void duplicateRegistrationReturnsProblem409() throws Exception {
        when(auth.register(any())).thenThrow(new ConflictException("Email already in use"));
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    @ParameterizedTest @ValueSource(strings={"name","email","password"})
    void invalidRegistrationIsRejectedBeforeUseCase(String field) throws Exception {
        String body=switch(field) {
            case "name" -> registration.replace("John"," ");
            case "email" -> registration.replace("john@example.com","invalid");
            default -> registration.replace("Strong@123","weak");
        };
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors."+field).exists());
        verifyNoInteractions(auth);
    }

    @ParameterizedTest @ValueSource(strings={"email","password"})
    void invalidLoginIsRejectedBeforeUseCase(String field) throws Exception {
        String body=field.equals("email")?login.replace("john@example.com","invalid"):login.replace("Strong@123","");
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors."+field).exists());
        verifyNoInteractions(auth);
    }

    @Test void meUsesPrincipalSubjectAndNeverAcceptsUserIdHeader() throws Exception {
        when(auth.findAuthenticatedUser(7L)).thenReturn(user);
        mvc.perform(get("/auth/me").principal(new UsernamePasswordAuthenticationToken("7",null)).header("X-User-Id","99"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.userType.name").value("CUSTOMER"));
        verify(auth).findAuthenticatedUser(7L);
        verify(auth,never()).findAuthenticatedUser(99L);
    }

    @Test void meMissingUserReturns404() throws Exception {
        when(auth.findAuthenticatedUser(7L)).thenThrow(new ResourceNotFoundException("User",7L));
        mvc.perform(get("/auth/me").principal(new UsernamePasswordAuthenticationToken("7",null)))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }

    @Test void malformedJsonReturns400() throws Exception {
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(auth);
    }
}
