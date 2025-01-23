package com.careerconnect.util;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    public ResponseCookie generateRefreshTokenCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
            .httpOnly(false)
            .secure(false) // cho HTTPS
            .path("/")
            .maxAge(7 * 24 * 60 * 60) // 7 days
            .sameSite("Lax")
            .build();
    }

    public ResponseCookie getCleanRefreshTokenCookie() {
        return ResponseCookie.from("refresh_token", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .build();
    }
}