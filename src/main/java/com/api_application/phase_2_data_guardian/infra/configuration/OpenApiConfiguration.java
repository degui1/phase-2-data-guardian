package com.api_application.phase_2_data_guardian.infra.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import java.util.Set;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI dataGuardianOpenApi() {
        Schema<?> problem = new Schema<>().types(Set.of("object"))
                .addProperty("type", new Schema<>().types(Set.of("string")).format("uri"))
                .addProperty("title", new Schema<>().types(Set.of("string")))
                .addProperty("status", new Schema<>().types(Set.of("integer")).format("int32"))
                .addProperty("detail", new Schema<>().types(Set.of("string")))
                .addProperty("instance", new Schema<>().types(Set.of("string")).format("uri"))
                .addProperty("code", new Schema<>().types(Set.of("string")))
                .addProperty("errors", new Schema<>().types(Set.of("object")).additionalProperties(true));
        return new OpenAPI()
                .info(new Info().title("Data Guardian — Tech Challenge Fase 2")
                        .version("0.0.1-SNAPSHOT")
                        .description("API de restaurantes, cardápios e tipos de usuário. IDs Long; preços em centavos. "
                                + "Use o token retornado por POST /auth/register no botão Authorize. "
                                + "Login ainda não implementado. Todas as rotas de negócio, exceto registro e login, exigem JWT."))
                .components(new Components().addSchemas("ApiProblem", problem)
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    public OperationCustomizer problemResponses() {
        return (operation, handler) -> {
            boolean publicAuth = handler.getBeanType().getSimpleName().equals("AuthController")
                    && !handler.getMethod().getName().equals("me");
            if (publicAuth) {
                operation.setSecurity(List.of());
            } else {
                operation.getResponses().addApiResponse("401", problem("Token ausente, inválido ou expirado"));
                operation.getResponses().addApiResponse("403", problem("Operação não autorizada"));
            }
            operation.getResponses().addApiResponse("400", problem("Dados ou parâmetros inválidos"));
            if (!handler.getMethod().getName().equals("register") && !handler.getMethod().getName().equals("login")) {
                operation.getResponses().addApiResponse("404", problem("Recurso não encontrado no contexto informado"));
            }
            if (!handler.hasMethodAnnotation(GetMapping.class) && !handler.getMethod().getName().equals("login")) {
                operation.getResponses().addApiResponse("409", problem("Conflito com dados existentes ou recurso em uso"));
            }
            return operation;
        };
    }

    private ApiResponse problem(String description) {
        return new ApiResponse().description(description).content(new Content()
                .addMediaType("application/problem+json", new MediaType()
                        .schema(new Schema<>().$ref("#/components/schemas/ApiProblem"))));
    }
}