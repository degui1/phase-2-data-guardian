package com.api_application.phase_2_data_guardian.infra.security.adapters;

import com.api_application.phase_2_data_guardian.application.model.auth.AuthenticatedUser;
import com.api_application.phase_2_data_guardian.application.model.auth.TokenData;
import com.api_application.phase_2_data_guardian.application.security.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JwtTokenProviderAdapter implements TokenProvider {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final Long expirationSeconds;

    public JwtTokenProviderAdapter(
            JwtEncoder jwtEncoder,

            @Value("${security.jwt.issuer}")
            String issuer,

            @Value("${security.jwt.expiration-seconds}")
            Long expirationSeconds
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public TokenData generate(AuthenticatedUser user) {
        Instant instantAt = Instant.now();
        Instant expiresAt = instantAt.plusSeconds(expirationSeconds);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(instantAt)
                .expiresAt(expiresAt)
                .subject(user.id().toString())
                .claim("email", user.email())
                .claim("user_type_id", user.userTypeId())
                .claim("user_type", user.userTypeName())
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        String accessToken = jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();

        return new TokenData(
                accessToken,
                expirationSeconds
        );
    }
}
