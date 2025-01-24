package com.careerconnect.util;

import com.careerconnect.entity.User;
import com.careerconnect.repository.UserRepository;
import com.careerconnect.security.CustomUserDetails;
import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationHelper {
    private final UserRepository userRepository;
    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            switch (authentication) {
                case UsernamePasswordAuthenticationToken authenticationToken -> {
                    CustomUserDetails customUserDetails=(CustomUserDetails)authentication.getPrincipal();
                    return customUserDetails.getUserId();
                }
                default -> {
                    // Xử lý trường hợp không xác định
                    return null;
                }
            }
        }
        return null; // Hoặc xử lý trường hợp không tìm thấy userId
    }
}