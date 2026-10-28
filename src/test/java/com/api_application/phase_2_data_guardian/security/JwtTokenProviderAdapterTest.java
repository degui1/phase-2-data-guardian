package com.api_application.phase_2_data_guardian.security;

import com.api_application.phase_2_data_guardian.application.model.auth.AuthenticatedUser;
import com.api_application.phase_2_data_guardian.infra.configuration.JwtConfiguration;
import com.api_application.phase_2_data_guardian.infra.security.adapters.JwtTokenProviderAdapter;
import java.time.Instant;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderAdapterTest {
    final JwtConfiguration config=new JwtConfiguration();
    final String secret=Base64.getEncoder().encodeToString("0123456789abcdef0123456789abcdef".getBytes(java.nio.charset.StandardCharsets.UTF_8));
    final javax.crypto.SecretKey key=config.secretKey(secret);
    final JwtEncoder encoder=config.jwtEncoder(key);
    final JwtDecoder decoder=config.jwtDecoder(key,"test-issuer");
    final JwtTokenProviderAdapter provider=new JwtTokenProviderAdapter(encoder,"test-issuer",3600L);

    @Test void signedTokenIncludesUserIdTypeIssuerAndExpiration() {
        var user=new AuthenticatedUser(7L,"Owner","owner@example.com",2L,"RESTAURANT_OWNER");
        Instant before=Instant.now().minusSeconds(1);
        var token=provider.generate(user);
        var jwt=decoder.decode(token.accessToken());
        assertThat(jwt.getSubject()).isEqualTo("7");
        assertThat(jwt.getClaimAsString("email")).isEqualTo("owner@example.com");
        assertThat(((Number)jwt.getClaim("user_type_id")).longValue()).isEqualTo(2L);
        assertThat(jwt.getClaimAsString("user_type")).isEqualTo("RESTAURANT_OWNER");
        assertThat(jwt.getClaimAsString("iss")).isEqualTo("test-issuer");
        assertThat(jwt.getHeaders().get("alg")).isEqualTo("HS256");
        assertThat(jwt.getIssuedAt()).isAfterOrEqualTo(before);
        assertThat(jwt.getExpiresAt()).isEqualTo(jwt.getIssuedAt().plusSeconds(3600));
        assertThat(token.expiresIn()).isEqualTo(3600L);
    }

    @Test void anotherSigningKeyIsRejected() {
        String token=provider.generate(new AuthenticatedUser(7L,"User","user@example.com",3L,"CUSTOMER")).accessToken();
        var other=config.secretKey(Base64.getEncoder().encodeToString("fedcba9876543210fedcba9876543210".getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        assertThatThrownBy(() -> config.jwtDecoder(other,"test-issuer").decode(token)).isInstanceOf(JwtException.class);
    }

    @Test void anotherIssuerIsRejected() {
        String token=provider.generate(new AuthenticatedUser(7L,"User","user@example.com",3L,"CUSTOMER")).accessToken();
        assertThatThrownBy(() -> config.jwtDecoder(key,"different-issuer").decode(token)).isInstanceOf(JwtException.class);
    }

    @Test void missingSubjectIsRejectedEvenWithValidSignature() {
        var claims=JwtClaimsSet.builder().issuer("test-issuer").issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600)).build();
        String token=encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue();
        assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
    }

    @Test void shortKeyIsRejected() {
        assertThatThrownBy(() -> config.secretKey(Base64.getEncoder().encodeToString(new byte[16])))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test void invalidBase64IsRejected() {
        assertThatThrownBy(() -> config.secretKey("not valid base64!"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
