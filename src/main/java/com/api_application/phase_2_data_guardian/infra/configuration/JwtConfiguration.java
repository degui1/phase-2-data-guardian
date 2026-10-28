package com.api_application.phase_2_data_guardian.infra.configuration;

import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

@Configuration
public class JwtConfiguration {

    @Bean
    public SecretKey secretKey(
            @Value("${security.jwt.secret}")
            String base64secret
    ) {
        byte[] keyBytes = Base64
                .getDecoder()
                .decode(base64secret);

        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret must have at least 32 bytes");
        }

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(
            SecretKey secretKey
    ) {
        return NimbusJwtEncoder
                .withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(
            SecretKey secretKey,
            @Value("${security.jwt.issuer}")
            String issuer
    ) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        decoder.setJwtValidator(
                new org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator<>(
                        JwtValidators.createDefaultWithIssuer(issuer),
                        jwt -> {
                            try {
                                if (Long.parseLong(jwt.getSubject()) > 0) {
                                    return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success();
                                }
                            } catch (RuntimeException ignored) {
                                // Reject a token that cannot identify a user before it reaches a controller.
                            }
                            return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure(
                                    new org.springframework.security.oauth2.core.OAuth2Error("invalid_token", "Invalid user subject", null));
                        })
        );

        return decoder;
    }
}
