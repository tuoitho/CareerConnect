package com.careerconnect.service;

import com.careerconnect.dto.request.LoginRequest;
import com.careerconnect.dto.response.LoginResponse;
import com.careerconnect.security.CustomUserDetails;
import com.careerconnect.security.JwtTokenProvider;
import com.careerconnect.service.impl.UserService;
import com.careerconnect.util.AuthenticationHelper;
import com.careerconnect.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        String accessToken = tokenProvider.generateAccessToken(authentication);

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        LoginResponse.LoggedInUser loggedInUser = LoginResponse.LoggedInUser.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();

        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(loggedInUser)
                .build();
    }

    public String generateRefreshToken(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        return tokenProvider.generateRefreshToken(authentication);

    }

}
