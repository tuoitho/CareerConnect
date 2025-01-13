package com.careerconnect.service.impl;

import com.careerconnect.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtTokenProvider tokenProvider;

    public String generateAccessToken(Authentication authentication) {
        return tokenProvider.generateAccessToken(authentication);
    }
}
