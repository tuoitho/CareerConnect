package com.careerconnect.service;

import com.careerconnect.dto.request.LoginRequest;
import com.careerconnect.dto.response.LoginResponse;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.security.CustomUserDetails;
import com.careerconnect.security.JwtService;
import com.careerconnect.service.impl.UserService;
import com.careerconnect.util.AuthenticationHelper;
import com.careerconnect.util.CookieUtil;
import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthService {
    @Value("${cloudflare.turnstile.secret-key}")
    private String secretKey;
    private final WebClient webClient;

    private final AuthenticationManager authenticationManager;
    private final JwtService tokenProvider;

    public LoginResponse login(LoginRequest loginRequest, String captchaToken) {
        Boolean isCaptchaValid = verifyCaptcha(captchaToken).block();
        if (Boolean.FALSE.equals(isCaptchaValid)) {
            throw new AppException(ErrorCode.ROBOT_DETECTED);
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = tokenProvider.generateAccessToken(userDetails);


        LoginResponse.LoggedInUser loggedInUser = LoginResponse.LoggedInUser.builder()
                .userId(userDetails.getUserId())
                .username(userDetails.getUsername())
                .role(userDetails.getRole())
                .build();

        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(loggedInUser)
                .build();
    }
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication=null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Logger.log("Authentication: " + authentication.getPrincipal());
        Logger.log("Authentication: " + authentication.getDetails());
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = tokenProvider.generateAccessToken(user);

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

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return tokenProvider.generateRefreshToken(userDetails);

    }
    private Mono<Boolean> verifyCaptcha(String token) {
        Map<String, String> body = new HashMap<>();
        body.put("secret", secretKey);
        body.put("response", token);

        return webClient.post()
                .uri("/turnstile/v0/siteverify")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (Boolean) response.get("success"))
                .onErrorReturn(false); // Trả về false nếu có lỗi
    }


}
