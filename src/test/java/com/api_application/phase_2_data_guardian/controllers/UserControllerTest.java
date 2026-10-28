package com.api_application.phase_2_data_guardian.controllers;

import com.api_application.phase_2_data_guardian.application.model.user.UpdateUserCommand;
import com.api_application.phase_2_data_guardian.domain.entity.User;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.time.OffsetDateTime;
import java.util.List;
import com.api_application.phase_2_data_guardian.application.usecase.UserUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.domain.exception.ForbiddenOperationException;
import com.api_application.phase_2_data_guardian.infra.http.controllers.UserController;
import com.api_application.phase_2_data_guardian.infra.http.exception.CustomControllerAdvice;
import com.api_application.phase_2_data_guardian.infra.http.mapper.UserResponseMapper;
import com.api_application.phase_2_data_guardian.domain.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {
    final UserUseCase users = mock(UserUseCase.class);
    final UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken("7",null);
    final MockMvc mvc = MockMvcBuilders.standaloneSetup(new UserController(users, new UserResponseMapper()))
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new CustomControllerAdvice()).build();

    UserType type() { return new UserType(2L,"RESTAURANT_OWNER",null,null); }

    @Test void userTypeAssignmentForwardsActorSeparatelyFromTarget() throws Exception {
        when(users.assignUserType(7L,9L,2L)).thenThrow(new ForbiddenOperationException("Only own account"));
        mvc.perform(put("/users/9/user-type").principal(principal).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userTypeId\":2}"))
                .andExpect(status().isForbidden());
        verify(users).assignUserType(7L,9L,2L);
    }

    @Test void ownTypeAssignmentAndQueryMapResponse() throws Exception {
        when(users.assignUserType(7L,7L,2L)).thenReturn(type());
        when(users.findUserType(7L)).thenReturn(type());
        mvc.perform(put("/users/7/user-type").principal(principal).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userTypeId\":2}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(2));
        mvc.perform(get("/users/7/user-type")).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("RESTAURANT_OWNER"));
    }

    @Test void invalidTypeIdIsRejectedBeforeAssignment() throws Exception {
        mvc.perform(put("/users/7/user-type").principal(principal).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userTypeId\":0}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.userTypeId").exists());
        verifyNoInteractions(users);
    }
    private User profile() {
        var created = OffsetDateTime.parse("2026-10-01T10:00:00Z");
        return new User(7L,"Owner","owner@example.com","private-hash",2L,created,created);
    }

    @Test void directoryListsOnlyPublicSummaryAndPreservesPagination() throws Exception {
        var page = new PageImpl<>(List.of(profile()),PageRequest.of(1,1),3);
        when(users.findAll(any())).thenReturn(page);
        mvc.perform(get("/users?page=1&size=1&sort=name,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(7))
                .andExpect(jsonPath("$.content[0].name").value("Owner"))
                .andExpect(jsonPath("$.content[0].userTypeId").value(2))
                .andExpect(jsonPath("$.content[0].email").doesNotExist())
                .andExpect(jsonPath("$.content[0].password").doesNotExist())
                .andExpect(jsonPath("$.content[0].passwordHash").doesNotExist())
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.totalElements").value(3));
        var captor = ArgumentCaptor.forClass(org.springframework.data.domain.Pageable.class);
        verify(users).findAll(captor.capture());
        assertThat(captor.getValue().getPageNumber()).isEqualTo(1);
        assertThat(captor.getValue().getSort().getOrderFor("name").isDescending()).isTrue();
    }

    @Test void ownProfileNeverExposesPasswordOrHash() throws Exception {
        when(users.findById(7L,7L)).thenReturn(profile());
        mvc.perform(get("/users/7").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("owner@example.com"))
                .andExpect(jsonPath("$.userTypeId").value(2))
                .andExpect(jsonPath("$.createdAt").value("2026-10-01T10:00:00Z"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test void profileUpdateMapsOnlyAllowedFieldsAndReturnsSavedData() throws Exception {
        User updated = profile();
        updated.setName("Updated");
        updated.setEmail("updated@example.com");
        when(users.update(eq(7L),eq(7L),any())).thenReturn(updated);
        mvc.perform(put("/users/7").principal(principal).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\",\"email\":\"updated@example.com\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
        var captor = ArgumentCaptor.forClass(UpdateUserCommand.class);
        verify(users).update(eq(7L),eq(7L),captor.capture());
        assertThat(captor.getValue()).isEqualTo(new UpdateUserCommand("Updated","updated@example.com"));
    }

    @Test void invalidProfileIsRejectedBeforeService() throws Exception {
        mvc.perform(put("/users/7").principal(principal).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\" \",\"email\":\"invalid\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").exists());
        verifyNoInteractions(users);
    }

    @Test void otherAccountReadIsForbiddenAndDoesNotLeakProfile() throws Exception {
        when(users.findById(7L,9L)).thenThrow(new ForbiddenOperationException("Only own account"));
        mvc.perform(get("/users/9").principal(principal)).andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403)).andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test void duplicateEmailIsProblem409() throws Exception {
        when(users.update(eq(7L),eq(7L),any())).thenThrow(new ConflictException("Email already in use"));
        mvc.perform(put("/users/7").principal(principal).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Owner\",\"email\":\"taken@example.com\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("RESOURCE_CONFLICT"));
    }

    @Test void deletionReturns204AndForwardsPrincipalSeparately() throws Exception {
        mvc.perform(delete("/users/7").principal(principal)).andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(users).delete(7L,7L);
    }

    @Test void deletingAccountWithRestaurantsReturns409() throws Exception {
        doThrow(new ConflictException("Restaurants associated")).when(users).delete(7L,7L);
        mvc.perform(delete("/users/7").principal(principal)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test void missingOwnAccountReturns404() throws Exception {
        when(users.findById(7L,7L)).thenThrow(new com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException("User",7L));
        mvc.perform(get("/users/7").principal(principal)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

}
