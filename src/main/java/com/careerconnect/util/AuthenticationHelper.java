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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;


@Service
@RequiredArgsConstructor
public class AuthenticationHelper {
    private final UserRepository userRepository;
    public Long getUserId() {
        //if la websocket, authchannelInterceptor
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
                Object principal = jwtAuthenticationToken.getPrincipal();
                if (principal instanceof Jwt jwt){
                    Logger.log(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
                    return Long.parseLong(jwt.getClaims().get("userId").toString());
                }
            } else
            if (authentication instanceof UsernamePasswordAuthenticationToken) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof CustomUserDetails customUserDetails) {
                    return customUserDetails.getUserId();
                }
            } else if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
//                // Extract user ID from OAuth2User if available
//                String email = oauth2User.getAttribute("email");
//                if (email != null) {
//                    return userRepository.findByEmail(email)
//                            .map(User::getUserId)
//                            .orElse(null);
//                }
            } else if (authentication instanceof RememberMeAuthenticationToken) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof CustomUserDetails customUserDetails) {
                    return customUserDetails.getUserId();
                }
            }
        }
        return null; // Hoặc xử lý trường hợp không tìm thấy userId
    }

    public User getCurrentUser() {
        Long userId = getUserId();
        if (userId != null) {
            return userRepository.findById(userId).orElse(null);
        }
        return null;
    }
}