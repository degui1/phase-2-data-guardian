package com.api_application.phase_2_data_guardian.controllers;

import com.api_application.phase_2_data_guardian.application.usecase.UserTypeUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.domain.exception.ConflictException;
import com.api_application.phase_2_data_guardian.infra.http.controllers.UserTypeController;
import com.api_application.phase_2_data_guardian.infra.http.exception.CustomControllerAdvice;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserTypeControllerTest {
    final UserTypeUseCase types = mock(UserTypeUseCase.class);
    final UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken("7",null);
    final MockMvc mvc = MockMvcBuilders.standaloneSetup(new UserTypeController(types))
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new CustomControllerAdvice()).build();

    UserType type() { return new UserType(2L,"RESTAURANT_OWNER",null,null); }

    @Test void typeCreateUpdateAndDeleteForwardActor() throws Exception {
        when(types.create(eq(7L),any())).thenReturn(type());
        when(types.update(eq(7L),eq(2L),any())).thenReturn(type());
        mvc.perform(post("/user-types").principal(principal).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"RESTAURANT_OWNER\"}"))
                .andExpect(status().isCreated()).andExpect(header().string("Location","http://localhost/user-types/2"));
        mvc.perform(put("/user-types/2").principal(principal).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"RESTAURANT_OWNER\"}")).andExpect(status().isOk());
        mvc.perform(delete("/user-types/2").principal(principal)).andExpect(status().isNoContent());
        verify(types).create(eq(7L),any());
        verify(types).update(eq(7L),eq(2L),any());
        verify(types).delete(7L,2L);
    }

    @Test void typeQueriesAndDefaultPaginationAreMapped() throws Exception {
        when(types.findById(2L)).thenReturn(type());
        when(types.findAll(any())).thenReturn(new PageImpl<>(List.of(type()),PageRequest.of(0,10),1));
        mvc.perform(get("/user-types/2")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("RESTAURANT_OWNER"));
        mvc.perform(get("/user-types")).andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(2));
        var captor = ArgumentCaptor.forClass(Pageable.class);
        verify(types).findAll(captor.capture());
        assertThat(captor.getValue().getPageNumber()).isZero();
        assertThat(captor.getValue().getPageSize()).isEqualTo(10);
        assertThat(captor.getValue().getSort()).isEqualTo(Sort.by("name").ascending());
    }

    @Test void invalidTypeNameIsRejectedBeforeUseCase() throws Exception {
        mvc.perform(post("/user-types").principal(principal).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\" \"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.name").exists());
        verifyNoInteractions(types);
    }

    @Test void associatedTypeDeletionReturnsProblem409() throws Exception {
        doThrow(new ConflictException("Type in use")).when(types).delete(7L,2L);
        mvc.perform(delete("/user-types/2").principal(principal)).andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("RESOURCE_CONFLICT"));
    }
}
