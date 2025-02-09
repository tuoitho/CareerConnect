package com.careerconnect.controller;

import com.careerconnect.dto.response.TokenResponse;
import com.careerconnect.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refresh")
@RequiredArgsConstructor
public class RefreshTokenController {
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    @PostMapping("")
    public ResponseEntity<?> refreshToken(
//            @RequestParam String refreshToken,
                                          @CookieValue("refreshToken") String refreshToken) {
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String newAccessToken = tokenProvider.generateAccessToken(authentication);
        return ResponseEntity.ok(new TokenResponse(newAccessToken));
    }
}
