package com.careerconnect.controller;

import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.dto.request.LoginRequest;
import com.careerconnect.dto.request.RegisterRequest;
import com.careerconnect.dto.response.LoginResponse;
import com.careerconnect.entity.User;
import com.careerconnect.security.CustomUserDetails;
import com.careerconnect.security.JwtTokenProvider;
import com.careerconnect.service.impl.UserService;
import com.careerconnect.util.AuthenticationHelper;
import com.careerconnect.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// Tạo controller để xử lý authentication
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final CookieUtil cookieUtil;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        LoginResponse.LoggedInUser loggedInUser = LoginResponse.LoggedInUser.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(loggedInUser)
                .build();
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

}