package com.api_application.phase_2_data_guardian.security;

import com.api_application.phase_2_data_guardian.domain.exception.*;
import com.api_application.phase_2_data_guardian.infra.configuration.*;
import com.api_application.phase_2_data_guardian.infra.http.exception.CustomControllerAdvice;
import java.time.Instant;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HttpSecurityTests {
    AnnotationConfigWebApplicationContext context;
    MockMvc mvc;

    @BeforeEach void setup() {
        context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context,
                "security.jwt.secret=ZGV2ZWxvcG1lbnQtb25seS1zZWNyZXQtY2hhbmdlLW1lLTEyMzQ1Ng==",
                "security.jwt.issuer=test");
        context.register(Config.class);
        context.refresh();
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }
    @AfterEach void close() { context.close(); }

    String token(String subject, Instant expiresAt) {
        var claims = JwtClaimsSet.builder().issuer("test").subject(subject)
                .issuedAt(Instant.now().minusSeconds(7200)).expiresAt(expiresAt).build();
        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        return context.getBean(JwtEncoder.class).encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
    String validToken() { return token("1", Instant.now().plusSeconds(3600)); }

    @Test void missingTokenIsProblem401() throws Exception {
        mvc.perform(get("/probe/ok")).andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate","Bearer"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }
    @Test void invalidTokenIsProblem401() throws Exception {
        mvc.perform(get("/probe/ok").header("Authorization","Bearer invalid"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
    }
    @Test void expiredTokenIsProblem401() throws Exception {
        mvc.perform(get("/probe/ok").header("Authorization","Bearer "+token("1", Instant.now().minusSeconds(3600))))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
    }
    @Test void nonNumericAndNegativeSubjectsAre401() throws Exception {
        for (String subject : new String[] {"abc", "-1", "0", "9223372036854775808"}) {
            mvc.perform(get("/probe/ok").header("Authorization","Bearer "+token(subject, Instant.now().plusSeconds(3600))))
                    .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
        }
    }
    @Test void validTokenReachesController() throws Exception {
        mvc.perform(get("/probe/ok").header("Authorization","Bearer "+validToken()))
                .andExpect(status().isOk());
    }
    @Test void businessRuleIsProblem400() throws Exception { problem("business",400); }
    @Test void forbiddenOperationIsProblem403() throws Exception { problem("forbidden",403); }
    @Test void filterAccessDeniedIsProblem403() throws Exception { problem("denied",403); }
    @Test void missingResourceIsProblem404() throws Exception { problem("missing",404); }
    @Test void conflictIsProblem409() throws Exception { problem("conflict",409); }
    @Test void foreignKeyConflictIsProblem409() throws Exception { problem("integrity",409); }
    @Test void malformedBodyIsProblem400() throws Exception {
        mvc.perform(post("/probe/body").header("Authorization","Bearer "+validToken())
                        .contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }
    @Test void invalidPathIsProblem400() throws Exception {
        mvc.perform(get("/probe/id/abc").header("Authorization","Bearer "+validToken()))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }
    void problem(String path, int status) throws Exception {
        mvc.perform(get("/probe/"+path).header("Authorization","Bearer "+validToken()))
                .andExpect(status().is(status)).andExpect(jsonPath("$.status").value(status))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Configuration
    @EnableWebMvc
    @Import({SecurityConfiguration.class, JwtConfiguration.class, CustomControllerAdvice.class, Probe.class})
    static class Config {}

    @RestController
    static class Probe {
        @GetMapping("/probe/{kind}")
        String get(@PathVariable String kind) {
            return switch (kind) {
                case "business" -> throw new BusinessRuleException("Invalid hours");
                case "forbidden" -> throw new ForbiddenOperationException("Not owner");
                case "denied" -> throw new AccessDeniedException("Denied");
                case "missing" -> throw new ResourceNotFoundException("Restaurant",1L);
                case "conflict" -> throw new ConflictException("In use");
                case "integrity" -> throw new org.springframework.dao.DataIntegrityViolationException("private SQL");
                default -> "ok";
            };
        }
        @PostMapping("/probe/body")
        String body(@RequestBody java.util.Map<String, String> body) { return "ok"; }
        @GetMapping("/probe/id/{id}")
        String id(@PathVariable Long id) { return "ok"; }
    }
}