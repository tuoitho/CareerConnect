package com.careerconnect.security;

import com.careerconnect.exception.CustomJwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

// Tạo class để xử lý JWT
@Component
public class JwtTokenProvider {
    @Value("${jwt.jwtSecret}")
    private String jwtSecret;
    @Value("${jwt.jwtExpirationInMs}")
    private long jwtExpirationInMs;
    @Value("${jwt.jwtRefreshExpirationInMs}")
    private long jwtRefreshExpirationInMs;

    public String generateAccessToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("type", TokenType.ACCESS)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
//                .setIssuedAt(now)
//                .setExpiration(new Date(System.currentTimeMillis() + 11))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    public String getTokenType(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("type", String.class);
    }
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
    }

    public String generateRefreshToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpirationInMs);
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("type", TokenType.REFRESH)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException signatureException) {
            throw new CustomJwtException("Invalid JWT signature");
        } catch (MalformedJwtException malformedJwtException) {
            throw new CustomJwtException("Invalid JWT token");
        } catch (ExpiredJwtException expiredJwtException) {
            throw new CustomJwtException("Expired JWT token");
        } catch (UnsupportedJwtException unsupportedJwtException) {
            throw new CustomJwtException("Unsupported JWT token");
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new CustomJwtException("JWT claims string is empty");
        }
    }
    public static enum TokenType {
        ACCESS, REFRESH
    }
}
