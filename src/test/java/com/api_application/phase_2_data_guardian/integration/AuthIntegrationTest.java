package com.api_application.phase_2_data_guardian.integration;

import com.api_application.phase_2_data_guardian.application.repository.UserRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {
    @Autowired WebApplicationContext context;
    @Autowired UserRepository users;
    @Autowired UserTypeRepository types;
    @Autowired JwtDecoder decoder;
    final JsonMapper json=JsonMapper.builder().build();
    MockMvc mvc;
    Long ownerTypeId;
    static final String PASSWORD="Strong@123";

    @BeforeEach void prepare() {
        types.save(new UserType("CUSTOMER"));
        ownerTypeId=types.save(new UserType("RESTAURANT_OWNER")).getId();
        mvc=MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    JsonNode register(String email) throws Exception {
        var response=mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("name","John","email",email,"password",PASSWORD))))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(response);
    }

    JsonNode login(String email,String password) throws Exception {
        var response=mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("email",email,"password",password))))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return json.readTree(response);
    }

    @Test void registerLoginAndMeWorkWithRealPasswordHashAndJwt() throws Exception {
        var registered=register("john@example.com");
        var stored=users.findByEmailIgnoreCase("john@example.com").orElseThrow();
        assertThat(stored.getPassword()).isNotEqualTo(PASSWORD).startsWith("$2");
        var logged=login("JOHN@EXAMPLE.COM",PASSWORD);
        assertThat(logged.path("tokenType").asText()).isEqualTo("Bearer");
        assertThat(logged.path("expiresIn").asLong()).isEqualTo(3600);
        assertThat(logged.path("user").path("id").asLong()).isEqualTo(stored.getId());
        assertThat(logged.path("user").path("userType").path("name").asText()).isEqualTo("CUSTOMER");
        String token=logged.path("accessToken").asText();
        assertThat(decoder.decode(token).getSubject()).isEqualTo(stored.getId().toString());
        mvc.perform(get("/auth/me").header("Authorization","Bearer "+token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.id").value(registered.path("user").path("id").asLong()));
    }

    @Test void wrongPasswordAndUnknownEmailReturnIdenticalGeneric401() throws Exception {
        register("john@example.com");
        String detail=null;
        for (String email:new String[]{"john@example.com","unknown@example.com"}) {
            String response=mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(Map.of("email",email,"password","Wrong@123"))))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                    .andReturn().getResponse().getContentAsString();
            String current=json.readTree(response).path("detail").asText();
            if(detail!=null) assertThat(current).isEqualTo(detail);
            detail=current;
            assertThat(response).doesNotContain("accessToken","stored-hash",email);
        }
    }

    @Test void duplicateEmailIgnoringCaseReturns409AndDoesNotCreateAnotherUser() throws Exception {
        register("john@example.com");
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("name","Another","email","JOHN@example.com","password",PASSWORD))))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
        assertThat(users.findAll(org.springframework.data.domain.PageRequest.of(0,10)).getTotalElements()).isEqualTo(1);
    }

    @Test void loginIssuesCurrentTypeAfterSelfServiceTypeChange() throws Exception {
        var registered=register("owner@example.com");
        long id=registered.path("user").path("id").asLong();
        String oldToken=registered.path("accessToken").asText();
        mvc.perform(put("/users/"+id+"/user-type").header("Authorization","Bearer "+oldToken)
                        .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("userTypeId",ownerTypeId))))
                .andExpect(status().isOk());
        var logged=login("owner@example.com",PASSWORD);
        var jwt=decoder.decode(logged.path("accessToken").asText());
        assertThat(jwt.getClaimAsString("user_type")).isEqualTo("RESTAURANT_OWNER");
        assertThat(((Number)jwt.getClaim("user_type_id")).longValue()).isEqualTo(ownerTypeId);
        assertThat(logged.path("user").path("userType").path("id").asLong()).isEqualTo(ownerTypeId);
        mvc.perform(get("/auth/me").header("Authorization","Bearer "+oldToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.userType.name").value("RESTAURANT_OWNER"));
    }

    @Test void bearerTokenCannotBeUsedToReadAnotherFullProfile() throws Exception {
        var first=register("first@example.com");
        var second=register("second@example.com");
        mvc.perform(get("/users/"+second.path("user").path("id").asLong())
                        .header("Authorization","Bearer "+first.path("accessToken").asText()))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test void missingTokenAndInvalidLoginBodyAreRejected() throws Exception {
        mvc.perform(get("/auth/me")).andExpect(status().isUnauthorized());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.email").exists());
        assertThat(users.findAll(org.springframework.data.domain.PageRequest.of(0,10)).getTotalElements()).isZero();
    }

    @Test void deletedAccountCannotLoginOrResolveAuthenticatedUser() throws Exception {
        var registered=register("deleted@example.com");
        String token=registered.path("accessToken").asText();
        long id=registered.path("user").path("id").asLong();
        mvc.perform(delete("/users/"+id).header("Authorization","Bearer "+token))
                .andExpect(status().isNoContent());
        mvc.perform(get("/auth/me").header("Authorization","Bearer "+token))
                .andExpect(status().isNotFound());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("email","deleted@example.com","password",PASSWORD))))
                .andExpect(status().isUnauthorized());
    }
}
