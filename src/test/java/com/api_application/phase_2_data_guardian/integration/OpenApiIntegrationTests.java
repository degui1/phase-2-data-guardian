package com.api_application.phase_2_data_guardian.integration;

import com.api_application.phase_2_data_guardian.application.usecase.*;
import com.api_application.phase_2_data_guardian.infra.configuration.*;
import com.api_application.phase_2_data_guardian.infra.http.controllers.*;
import com.api_application.phase_2_data_guardian.infra.http.exception.CustomControllerAdvice;
import com.api_application.phase_2_data_guardian.infra.http.mapper.MenuItemResponseMapper;
import com.api_application.phase_2_data_guardian.infra.http.mapper.UserResponseMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = OpenApiIntegrationTests.DocsApplication.class, properties = {
        "security.jwt.secret=ZGV2ZWxvcG1lbnQtb25seS1zZWNyZXQtY2hhbmdlLW1lLTEyMzQ1Ng==",
        "security.jwt.issuer=docs-test", "springdoc.override-with-generic-response=false"
})
class OpenApiIntegrationTests {
    @Autowired WebApplicationContext context;

    MockMvc mvc() {
        return MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test void documentationIsPublicAndDescribesTheRealContract() throws Exception {
        mvc().perform(get("/v3/api-docs"))
                .andDo(result -> java.nio.file.Files.writeString(java.nio.file.Path.of("target/openapi.json"), result.getResponse().getContentAsString()))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.info.title").value("Data Guardian — Tech Challenge Fase 2"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.security[0].bearerAuth").isArray())
                .andExpect(jsonPath("$.paths['/auth/register'].post.security").isEmpty())
                .andExpect(jsonPath("$.paths['/auth/register'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/auth/register'].post.requestBody").exists())
                .andExpect(jsonPath("$.paths['/auth/login'].post.deprecated").value(true))
                .andExpect(jsonPath("$.paths['/auth/me'].get.responses['401']").exists())
                .andExpect(jsonPath("$.paths['/restaurants'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/restaurants'].get.parameters[?(@.name == 'page')]").isNotEmpty())
                .andExpect(jsonPath("$.paths['/restaurants'].get.parameters[?(@.name == 'size')]").isNotEmpty())
                .andExpect(jsonPath("$.paths['/restaurants'].get.parameters[?(@.name == 'sort')]").isNotEmpty())
                .andExpect(jsonPath("$.paths['/restaurants/{restaurantId}/menu-items/{menuItemId}'].get.parameters[?(@.name == 'restaurantId')]").isNotEmpty())
                .andExpect(jsonPath("$.paths['/restaurants/{restaurantId}/menu-items/{menuItemId}'].get.parameters[?(@.name == 'menuItemId')]").isNotEmpty())
                .andExpect(jsonPath("$.paths['/restaurants/{restaurantId}/menu-items/{menuItemId}'].delete.responses['204']").exists())
                .andExpect(jsonPath("$.paths['/user-types'].post.responses['403'].content['application/problem+json']").exists())
                .andExpect(jsonPath("$.components.schemas.ApiProblem.properties.status.type").value("integer"));
    }

    @Test void swaggerResourcesAndConfigArePublic() throws Exception {
        mvc().perform(get("/swagger-ui.html")).andExpect(status().is3xxRedirection());
        mvc().perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        mvc().perform(get("/v3/api-docs/swagger-config")).andExpect(status().isOk());
        mvc().perform(get("/v3/api-docs.yaml")).andExpect(status().isOk());
    }

    @Test void businessEndpointsRemainProtected() throws Exception {
        mvc().perform(get("/restaurants")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
        mvc().perform(get("/auth/me")).andExpect(status().isUnauthorized());
    }

    @Configuration
    @EnableAutoConfiguration(excludeName = {
            "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
            "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
            "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration"
    })
    @Import({OpenApiConfiguration.class, SecurityConfiguration.class, JwtConfiguration.class,
            CustomControllerAdvice.class, MenuItemResponseMapper.class, UserResponseMapper.class, AuthController.class, UserController.class,
            UserTypeController.class, RestaurantController.class, MenuItemController.class})
    static class DocsApplication {
        @Bean AuthUseCase authUseCase() { return mock(AuthUseCase.class); }
        @Bean UserUseCase userUseCase() { return mock(UserUseCase.class); }
        @Bean UserTypeUseCase userTypeUseCase() { return mock(UserTypeUseCase.class); }
        @Bean RestaurantUseCase restaurantUseCase() { return mock(RestaurantUseCase.class); }
        @Bean MenuItemUseCase menuItemUseCase() { return mock(MenuItemUseCase.class); }
    }
}