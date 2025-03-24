package com.careerconnect.security;

import com.careerconnect.exception.CustomJwtException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.jwtSecret}")
    private String secretKey;
    @Value("${jwt.jwtExpirationInMs}")
    private long jwtExpirationInMs;
    @Value("${jwt.jwtRefreshExpirationInMs}")
    private long jwtRefreshExpirationInMs;

    private final TokenBlacklistService tokenBlacklistService;

    public String generateAccessToken(CustomUserDetails userDetails) {
        return generateAccessToken(Map.of(), userDetails);
    }

    public String generateAccessToken(Map<String, Object> extraClaims, CustomUserDetails userDetails) {
        try {
            // Create JWT signer with secret key
            JWSSigner signer = new MACSigner(secretKey);
            
            // Build JWT claims
            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + jwtExpirationInMs));

            // Add authorities/roles from CustomUserDetails
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role) // Loại bỏ tiền tố "ROLE_" nếu có
                    .collect(Collectors.toList());
            claimsBuilder.claim("roles", roles);

            claimsBuilder.claim("userId", userDetails.getUserId());
            
            // Add any extra claims
            extraClaims.forEach(claimsBuilder::claim);
            
            // Create and sign JWT
            SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.HS256).build(),
                claimsBuilder.build()
            );
            signedJWT.sign(signer);
            
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error generating token", e);
        }
    }
    public String generateRefreshToken(CustomUserDetails userDetails) {
        return generateRefreshToken(Map.of(), userDetails);
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, CustomUserDetails userDetails) {
        try {
            // Create JWT signer with secret key
            JWSSigner signer = new MACSigner(secretKey);
            // Build JWT claims
            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                    .subject(userDetails.getUsername())
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + jwtRefreshExpirationInMs));

            //ko cần roles


            // Add userId
            claimsBuilder.claim("userId", userDetails.getUserId());

            // Add any extra claims
            extraClaims.forEach(claimsBuilder::claim);

            // Create and sign JWT
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.HS256).build(),
                    claimsBuilder.build()
            );
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    public String extractUsername(String token) {
        try {
            // Parse and decode JWT
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            
            // Extract username from subject claim
            return claimsSet.getSubject();
        } catch (ParseException e) {
            throw new RuntimeException("Error extracting username from token", e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            if (tokenBlacklistService.isBlacklisted(token)) {
                throw new CustomJwtException("Token has been blacklisted");
            }
            // Parse JWT and create verifier
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secretKey);

            // Verify JWT signature
            if (!signedJWT.verify(verifier)) {
                return false;
            }

            // Get claims set
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            String username = claimsSet.getSubject();
            Date expiration = claimsSet.getExpirationTime();
            Date now = new Date();

            // Validate username and expiration
            return username.equals(userDetails.getUsername()) 
                && expiration != null 
                && expiration.after(now);
                
        } catch (ParseException | JOSEException e) {
            return false;
        }
    }

    public JWTClaimsSet decodeToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secretKey);
            
            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("Invalid token signature");
            }
            
            return signedJWT.getJWTClaimsSet();
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException("Error decoding token", e);
        }
    }

    public String getJwtFromRequest(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
//        return null;
        throw new CustomJwtException("Invalid token");
    }
}
