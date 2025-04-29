package com.careerconnect.service;

import com.careerconnect.dto.request.LoginRequest;
import com.careerconnect.dto.response.LoginResponse;
import com.careerconnect.entity.Candidate;
import com.careerconnect.entity.Role;
import com.careerconnect.entity.User;
import com.careerconnect.enums.RoleEnum;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.repository.RoleRepository;
import com.careerconnect.repository.UserRepository;
import com.careerconnect.security.CustomUserDetails;
import com.careerconnect.security.CustomUserDetailsService;
import com.careerconnect.security.JwtService;
import com.careerconnect.service.impl.UserService;
import com.careerconnect.util.AuthenticationHelper;
import com.careerconnect.util.CookieUtil;
import com.careerconnect.util.Logger;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final RoleRepository roleRepository;
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
        Authentication authentication=authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

//        Logger.log("Authentication: " + authentication.getPrincipal());
//        Logger.log("Authentication: " + authentication.getDetails());
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
                .uri("https://challenges.cloudflare.com/turnstile/v0/siteverify")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (Boolean) response.get("success"))
                .onErrorReturn(false); // Trả về false nếu có lỗi
    }


    public LoginResponse loginGoogle(String email, String name, String pictureUrl, HttpServletResponse response) {
        // Tạo user nếu chưa có
        User user=userRepository.findByEmail(email).orElseGet(() -> {
            Candidate candidate = new Candidate();
            candidate.setEmail(email);
            candidate.setFullname(name);
            candidate.setAvatar(pictureUrl);
            candidate.setRole(roleRepository.findByRoleName(RoleEnum.CANDIDATE).orElseGet(() -> roleRepository.save(Role.builder().roleName(RoleEnum.CANDIDATE).build())));
            candidate.setActive(true);
            return userRepository.save(candidate);
        });
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        String accessToken = tokenProvider.generateAccessToken(customUserDetails);

        LoginResponse.LoggedInUser loggedInUser = LoginResponse.LoggedInUser.builder()
                .userId(user.getUserId())
                .username(user.getEmail())
                .role(customUserDetails.getRole())
                .build();

        Logger.log("Logged in user: " + loggedInUser);
        String refreshToken = tokenProvider.generateRefreshToken(customUserDetails);
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge(365 * 24 * 60 * 60);
        cookie.setHttpOnly(true); // Prevent client-side JavaScript from accessing the cookie
        cookie.setSecure(true); // Set to true if using HTTPS
        cookie.setAttribute("SameSite", "None"); // Allow the cookie to be sent with same-site requests
        response.addCookie(cookie);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(loggedInUser)
                .build();
    }
}
