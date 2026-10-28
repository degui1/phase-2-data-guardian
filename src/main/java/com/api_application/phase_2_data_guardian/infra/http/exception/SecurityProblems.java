package com.api_application.phase_2_data_guardian.infra.http.exception;
import org.springframework.http.converter.json.ProblemDetailJacksonMixin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import org.springframework.http.ProblemDetail;
import tools.jackson.databind.json.JsonMapper;

public final class SecurityProblems {
    private static final JsonMapper MAPPER = JsonMapper.builder()
            .addMixIn(ProblemDetail.class, ProblemDetailJacksonMixin.class)
            .build();

    private SecurityProblems() {}

    public static void write(HttpServletRequest request, HttpServletResponse response, int status) throws IOException {
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle(status == 401 ? "Unauthorized" : "Access denied");
        problem.setDetail(status == 401 ? "A valid bearer token is required" : "Access to this resource is denied");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("code", status == 401 ? "UNAUTHORIZED" : "FORBIDDEN_OPERATION");
        response.setStatus(status);
        response.setContentType("application/problem+json");
        if (status == 401) {
            response.setHeader("WWW-Authenticate", "Bearer");
        }
        response.getWriter().write(MAPPER.writeValueAsString(problem));
    }
}