package com.careerconnect.controller;

import com.careerconnect.security.CustomUserDetails;
import com.careerconnect.security.JwtService;
import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GoogleController {
    private final JwtService jwtService;

    @GetMapping("/api/auth/google-login")
    public String googleLogin(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        Logger.log("Google login principal " , principal);
        return "jwt"; // Trả về JWT cho frontend
    }

    @GetMapping("/test")
    public OAuth2User getUser(@AuthenticationPrincipal OAuth2User principal) {
        return principal; // API kiểm tra thông tin user (tùy chọn)
    }
}
