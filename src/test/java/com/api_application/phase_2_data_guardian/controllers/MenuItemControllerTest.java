package com.api_application.phase_2_data_guardian.controllers;

import com.api_application.phase_2_data_guardian.application.usecase.MenuItemUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import com.api_application.phase_2_data_guardian.infra.http.controllers.MenuItemController;
import com.api_application.phase_2_data_guardian.infra.http.exception.CustomControllerAdvice;
import com.api_application.phase_2_data_guardian.infra.http.mapper.MenuItemResponseMapper;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MenuItemControllerTest {
    final MenuItemUseCase useCase = mock(MenuItemUseCase.class);
    final MockMvc mvc = MockMvcBuilders.standaloneSetup(new MenuItemController(useCase, new MenuItemResponseMapper()))
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new CustomControllerAdvice()).build();
    final UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken("1", null);
    final String body = """
            {"name":"Item","description":"Description","priceInCents":100,"dineInOnly":false,"imagePath":"photo"}
            """;

    @Test void getForwardsBothIdsAndReturns404ForWrongRestaurant() throws Exception {
        when(useCase.findById(99L, 20L)).thenThrow(new ResourceNotFoundException("Menu item",20L));
        mvc.perform(get("/restaurants/99/menu-items/20"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
        verify(useCase).findById(99L, 20L);
    }
    @Test void putForwardsBothIds() throws Exception {
        when(useCase.update(eq(1L), eq(99L), eq(20L), any()))
                .thenThrow(new ResourceNotFoundException("Menu item",20L));
        mvc.perform(put("/restaurants/99/menu-items/20").principal(user)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
        verify(useCase).update(eq(1L), eq(99L), eq(20L), any());
    }
    @Test void deleteForwardsBothIds() throws Exception {
        doThrow(new ResourceNotFoundException("Menu item",20L)).when(useCase).delete(1L,99L,20L);
        mvc.perform(delete("/restaurants/99/menu-items/20").principal(user))
                .andExpect(status().isNotFound());
        verify(useCase).delete(1L,99L,20L);
    }
    @Test void invalidBodyProducesValidationProblem() throws Exception {
        mvc.perform(post("/restaurants/10/menu-items").principal(user)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.name").exists());
        verifyNoInteractions(useCase);
    }
    @Test void creationReturns201AndLocation() throws Exception {
        MenuItem item = new MenuItem(10L,"Item","Description",100L,false,"photo");
        item.setId(20L);
        when(useCase.create(eq(1L), any())).thenReturn(item);
        mvc.perform(post("/restaurants/10/menu-items").principal(user)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location","http://localhost/restaurants/10/menu-items/20"))
                .andExpect(jsonPath("$.restaurantId").value(10));
    }
    @Test void successfulDeleteReturns204() throws Exception {
        mvc.perform(delete("/restaurants/10/menu-items/20").principal(user))
                .andExpect(status().isNoContent());
    }
    private MenuItem completeItem() {
        return new MenuItem(20L, 10L, "Item", "Description", 2590L, true, "/item.jpg",
                OffsetDateTime.parse("2026-10-27T10:00:00Z"),
                OffsetDateTime.parse("2026-10-27T11:00:00Z"));
    }

    @Test void getReturnsCompleteResponseContract() throws Exception {
        when(useCase.findById(10L,20L)).thenReturn(completeItem());
        mvc.perform(get("/restaurants/10/menu-items/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.restaurantId").value(10))
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.priceInCents").value(2590))
                .andExpect(jsonPath("$.dineInOnly").value(true))
                .andExpect(jsonPath("$.imagePath").value("/item.jpg"))
                .andExpect(jsonPath("$.createdAt").value("2026-10-27T10:00:00Z"))
                .andExpect(jsonPath("$.updatedAt").value("2026-10-27T11:00:00Z"));
    }

    @Test void updateReturnsSavedItemRatherThanRequestData() throws Exception {
        when(useCase.update(eq(1L),eq(10L),eq(20L),any())).thenReturn(completeItem());
        mvc.perform(put("/restaurants/10/menu-items/20").principal(user)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.restaurantId").value(10))
                .andExpect(jsonPath("$.priceInCents").value(2590))
                .andExpect(jsonPath("$.dineInOnly").value(true))
                .andExpect(jsonPath("$.createdAt").value("2026-10-27T10:00:00Z"));
    }

    @Test void pageConversionPreservesResponseFieldsAndPagination() throws Exception {
        var page = new PageImpl<>(List.of(completeItem()),PageRequest.of(1,1),3);
        when(useCase.findByRestaurantId(eq(10L),any())).thenReturn(page);
        mvc.perform(get("/restaurants/10/menu-items?page=1&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(20))
                .andExpect(jsonPath("$.content[0].priceInCents").value(2590))
                .andExpect(jsonPath("$.content[0].updatedAt").value("2026-10-27T11:00:00Z"))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

}
