package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.dto.request.LoginRequest;
import com.careerconnect.dto.request.RegisterRequest;
import com.careerconnect.dto.response.LoginResponse;
import com.careerconnect.dto.response.TokenResponse;
import com.careerconnect.entity.User;
import com.careerconnect.security.CustomUserDetails;
import com.careerconnect.security.JwtTokenProvider;
import com.careerconnect.security.TokenBlacklistService;
import com.careerconnect.service.AuthService;
import com.careerconnect.service.impl.UserService;
import com.careerconnect.util.AuthenticationHelper;
import com.careerconnect.util.CookieUtil;
import com.careerconnect.util.Logger;
import com.cloudinary.Api;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

// Tạo controller để xử lý authentication
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoint.PREFIX+"/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final CookieUtil cookieUtil;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final AuthService authService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, @RequestParam(value = "tk") String token, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(loginRequest,token);

        String refreshToken = authService.generateRefreshToken(loginRequest);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge(365 * 24 * 60 * 60);
        cookie.setHttpOnly(true); // Prevent client-side JavaScript from accessing the cookie
        cookie.setSecure(true); // Set to true if using HTTPS
        cookie.setAttribute("SameSite", "Lax"); // Allow the cookie to be sent with same-site requests
        response.addCookie(cookie);

        return ResponseEntity.ok().body(loginResponse);

    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
        var response = ApiResponse.builder()
                .message("User registered successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
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

    //logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refreshToken") String refreshToken, HttpServletRequest req, HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        String accessToken = tokenProvider.getJwtFromRequest(req);
        tokenBlacklistService.addAccessTokenToBlacklist(accessToken);
        tokenBlacklistService.addRefreshTokenToBlacklist(refreshToken);

        ApiResponse<String> apiResponse=ApiResponse.<String>builder().message("Logout successfully").build();
        return ResponseEntity.ok().body(apiResponse);
    }
}