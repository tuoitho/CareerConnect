package com.careerconnect.security;

import com.careerconnect.exception.CustomJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.jwtSecret}")
    private String jwtSecret;

    private final TokenBlacklistService tokenBlacklistService;
    private NimbusJwtDecoder jwtDecoder;

    @Override
    public Jwt decode(String token) throws JwtException {
        if (tokenBlacklistService.isBlacklisted(token)) {
            throw new CustomJwtException("Token has been blacklisted");
        }
        if (Objects.isNull(jwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(jwtSecret.getBytes(), "HS256");
            jwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();
        }
        return jwtDecoder.decode(token);
    }

}