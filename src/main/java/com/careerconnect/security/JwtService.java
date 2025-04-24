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
import org.springframework.security.oauth2.core.user.OAuth2User;
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

    private String generateToken(String subject, long expirationInMs, Map<String, Object> extraClaims, List<String> roles) {
        try {
            JWSSigner signer = new MACSigner(secretKey);
            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + expirationInMs));
            if (roles != null && !roles.isEmpty()) {
                claimsBuilder.claim("roles", roles);
            }
            extraClaims.forEach(claimsBuilder::claim);
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
    // Hàm mới: Refresh token và tạo accessToken mới
    public String refreshAccessToken(String refreshToken) {
        try {
            // Giải mã refreshToken
            SignedJWT signedJWT = SignedJWT.parse(refreshToken);
            JWSVerifier verifier = new MACVerifier(secretKey);

            // Kiểm tra chữ ký
            if (!signedJWT.verify(verifier)) {
                throw new CustomJwtException("Invalid refresh token signature");
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            String subject = claimsSet.getSubject(); // Email hoặc username
            Date expiration = claimsSet.getExpirationTime();
            Date now = new Date();

            // Kiểm tra hết hạn
            if (expiration == null || expiration.before(now)) {
                throw new CustomJwtException("Refresh token has expired");
            }

            // Kiểm tra blacklist
            if (tokenBlacklistService.isBlacklisted(refreshToken)) {
                throw new CustomJwtException("Refresh token has been blacklisted");
            }

            // Lấy userId nếu có
            String userId = claimsSet.getClaims().get("userId").toString();

            // Tạo accessToken mới
            Map<String, Object> extraClaims = userId != null ? Map.of("userId", userId) : Map.of();
            //lấy roles từ refreshToken cũ
//            List<String> roles = claimsSet.getStringListClaim("roles");
            List<String> roles =claimsSet.getClaims().get("roles")!=null?(List<String>) claimsSet.getClaims().get("roles"):null;

            return generateToken(subject, jwtExpirationInMs, extraClaims, roles);

        } catch (ParseException | JOSEException e) {
            e.printStackTrace();
            throw new CustomJwtException("Error processing refresh token: " + e.getMessage());
        }
    }

    public String generateAccessToken(OAuth2User oAuth2User) {
        return generateAccessToken(Map.of(), oAuth2User);
    }

    public String generateAccessToken(Map<String, Object> extraClaims, OAuth2User oAuth2User) {
        try {
            JWSSigner signer = new MACSigner(secretKey);

            String email = oAuth2User.getAttribute("email"); // Lấy email làm subject
            List<String> roles = oAuth2User.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                    .collect(Collectors.toList());

            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                    .subject(email)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                    .claim("roles", roles);

            // Nếu CustomOAuth2User có userId, thêm vào claims
            if (oAuth2User instanceof CustomOAuth2User customOAuth2User) {
                claimsBuilder.claim("userId", customOAuth2User.getUserId());
            }

            extraClaims.forEach(claimsBuilder::claim);

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.HS256).build(),
                    claimsBuilder.build()
            );
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error generating OAuth2 access token", e);
        }
    }

    // Hàm tạo refreshToken từ OAuth2User
    public String generateRefreshToken(OAuth2User oAuth2User) {
        return generateRefreshToken(Map.of(), oAuth2User);
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, OAuth2User oAuth2User) {
        try {
            JWSSigner signer = new MACSigner(secretKey);

            String email = oAuth2User.getAttribute("email"); // Lấy email làm subject

            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                    .subject(email)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + jwtRefreshExpirationInMs));

            // Nếu CustomOAuth2User có userId, thêm vào claims
            if (oAuth2User instanceof CustomOAuth2User customOAuth2User) {
                claimsBuilder.claim("userId", customOAuth2User.getUserId());
            }

            //role
            List<String> roles = oAuth2User.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                    .toList();
            claimsBuilder.claim("roles", roles);

            extraClaims.forEach(claimsBuilder::claim);

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.HS256).build(),
                    claimsBuilder.build()
            );
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error generating OAuth2 refresh token", e);
        }
    }

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

            //roles
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role) // Loại bỏ tiền tố "ROLE_" nếu có
                    .toList();
            claimsBuilder.claim("roles", roles);

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
