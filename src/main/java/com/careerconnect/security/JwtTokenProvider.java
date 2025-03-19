package com.careerconnect.security;

import com.careerconnect.exception.CustomJwtException;
import com.careerconnect.util.Logger;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

// Tạo class để xử lý JWT
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final TokenBlacklistService tokenBlacklistService;
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
//    public Date getExpirationDateFromToken(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.getExpiration();
//    }

//    public String getTokenType(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.get("type", String.class);
//    }
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
            //check black list
            if (tokenBlacklistService.isBlacklisted(token)) {
                throw new CustomJwtException("Token is blacklisted");
            }
            return true;
        } catch (SignatureException signatureException) {
            throw new CustomJwtException("Invalid JWT signature");
        } catch (MalformedJwtException | DecodingException malformedJwtException) {
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

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new CustomJwtException("Authorization header is required");
    }
}
