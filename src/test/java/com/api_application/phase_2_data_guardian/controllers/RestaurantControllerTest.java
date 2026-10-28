package com.api_application.phase_2_data_guardian.controllers;

import com.api_application.phase_2_data_guardian.application.usecase.RestaurantUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.Address;
import com.api_application.phase_2_data_guardian.domain.entity.OpeningHours;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.domain.exception.ForbiddenOperationException;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import com.api_application.phase_2_data_guardian.infra.http.controllers.RestaurantController;
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

class RestaurantControllerTest {
    final RestaurantUseCase restaurants = mock(RestaurantUseCase.class);
    final UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken("7",null);
    final MockMvc mvc = MockMvcBuilders.standaloneSetup(new RestaurantController(restaurants))
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new CustomControllerAdvice()).build();

    final String restaurantBody = """
            {"name":"Restaurant","address":{"street":"Street","number":"10","complement":null,
            "neighborhood":"Center","city":"City","state":"SP","zipCode":"01001-000"},
            "cuisineType":"Brazilian","openingHours":{"monday":[{"opensAt":"08:00:00","closesAt":"18:00:00"}],
            "tuesday":[],"wednesday":[],"thursday":[],"friday":[],"saturday":[],"sunday":[]}}
            """;

    Restaurant restaurant() {
        var result = new Restaurant("Restaurant",new Address("Street","10",null,"Center","City","SP","01001-000"),
                "Brazilian",new OpeningHours(),7L);
        result.setId(10L);
        return result;
    }

    @Test void restaurantCreateUsesPrincipalAndReturnsLocation() throws Exception {
        when(restaurants.create(any())).thenReturn(restaurant());
        mvc.perform(post("/restaurants").principal(principal).contentType(MediaType.APPLICATION_JSON).content(restaurantBody))
                .andExpect(status().isCreated()).andExpect(header().string("Location","http://localhost/restaurants/10"))
                .andExpect(jsonPath("$.ownerId").value(7)).andExpect(jsonPath("$.address.complement").doesNotExist());
        var captor = ArgumentCaptor.forClass(Restaurant.class);
        verify(restaurants).create(captor.capture());
        assertThat(captor.getValue().getOwnerId()).isEqualTo(7L);
        assertThat(captor.getValue().getOpeningHours().getMonday()).hasSize(1);
    }

    @Test void restaurantUpdateForwardsPrincipalAndPathId() throws Exception {
        when(restaurants.update(eq(7L),eq(10L),any())).thenReturn(restaurant());
        mvc.perform(put("/restaurants/10").principal(principal).contentType(MediaType.APPLICATION_JSON).content(restaurantBody))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(10));
        verify(restaurants).update(eq(7L),eq(10L),any());
    }

    @Test void restaurantFindReturnsNestedAddressAndHours() throws Exception {
        when(restaurants.findById(10L)).thenReturn(restaurant());
        mvc.perform(get("/restaurants/10")).andExpect(status().isOk())
                .andExpect(jsonPath("$.address.zipCode").value("01001-000"))
                .andExpect(jsonPath("$.openingHours.sunday").isEmpty());
    }

    @Test void restaurantListForwardsExplicitPaginationAndSorting() throws Exception {
        when(restaurants.findAll(any())).thenReturn(new PageImpl<>(List.of(restaurant()),PageRequest.of(1,2),5));
        mvc.perform(get("/restaurants?page=1&size=2&sort=cuisineType,desc"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(10));
        var captor = ArgumentCaptor.forClass(Pageable.class);
        verify(restaurants).findAll(captor.capture());
        assertThat(captor.getValue().getPageNumber()).isEqualTo(1);
        assertThat(captor.getValue().getPageSize()).isEqualTo(2);
        assertThat(captor.getValue().getSort().getOrderFor("cuisineType").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test void restaurantDeleteReturns204() throws Exception {
        mvc.perform(delete("/restaurants/10").principal(principal)).andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(restaurants).delete(7L,10L);
    }

    @Test void restaurantWithInvalidAddressIsRejectedBeforeUseCase() throws Exception {
        mvc.perform(post("/restaurants").principal(principal).contentType(MediaType.APPLICATION_JSON)
                        .content(restaurantBody.replace("01001-000","invalid")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors['address.zipCode']").exists());
        verifyNoInteractions(restaurants);
    }

    @Test void nullOpeningPeriodIsRejectedBeforeMapping() throws Exception {
        mvc.perform(post("/restaurants").principal(principal).contentType(MediaType.APPLICATION_JSON)
                        .content(restaurantBody.replace("{\"opensAt\":\"08:00:00\",\"closesAt\":\"18:00:00\"}","null")))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(restaurants);
    }

    @Test void restaurantMissingReturnsProblem404() throws Exception {
        when(restaurants.findById(10L)).thenThrow(new ResourceNotFoundException("Restaurant",10L));
        mvc.perform(get("/restaurants/10")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test void restaurantOwnerViolationReturnsProblem403() throws Exception {
        doThrow(new ForbiddenOperationException("Not owner")).when(restaurants).delete(7L,10L);
        mvc.perform(delete("/restaurants/10").principal(principal)).andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN_OPERATION"));
    }
}
