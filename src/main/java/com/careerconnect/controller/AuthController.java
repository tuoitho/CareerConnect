package com.careerconnect.controller;

import com.careerconnect.dto.request.GoogleLoginRequest;
import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.request.LoginRequest;
import com.careerconnect.dto.request.RegisterRequest;
import com.careerconnect.dto.response.LoginResponse;
import com.careerconnect.dto.response.TokenResponse;
import com.careerconnect.security.CustomUserDetailsService;
import com.careerconnect.security.JwtService;
import com.careerconnect.security.TokenBlacklistService;
import com.careerconnect.service.AuthService;
import com.careerconnect.service.impl.UserService;
import com.careerconnect.util.CookieUtil;
import com.careerconnect.util.Logger;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

// Tạo controller để xử lý authentication
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoint.PREFIX + "/auth")
public class AuthController {
    private final JwtService tokenProvider;
    private final CookieUtil cookieUtil;
    private final UserService userService;
    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

//    @PostMapping("/login")
//    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, @RequestParam(value = "tk") String token, HttpServletResponse response) {
//        LoginResponse loginResponse = authService.login(loginRequest, token);
//        String refreshToken = authService.generateRefreshToken(loginRequest);
//
//        Cookie cookie = new Cookie("refreshToken", refreshToken);
//        cookie.setPath("/");
//        cookie.setMaxAge(365 * 24 * 60 * 60);
//        cookie.setHttpOnly(true); // Prevent client-side JavaScript from accessing the cookie
//        cookie.setSecure(true); // Set to true if using HTTPS
//        cookie.setAttribute("SameSite", "None"); // Allow the cookie to be sent with same-site requests
//        response.addCookie(cookie);
//
//        return ResponseEntity.ok().body(loginResponse);
//
//    }
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(loginRequest);
        String refreshToken = authService.generateRefreshToken(loginRequest);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge(365 * 24 * 60 * 60);
        cookie.setHttpOnly(true); // Prevent client-side JavaScript from accessing the cookie
        cookie.setSecure(true); // Set to true if using HTTPS
        cookie.setAttribute("SameSite", "None"); // Allow the cookie to be sent with same-site requests
        response.addCookie(cookie);

        return ResponseEntity.ok().body(loginResponse);

    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
        var response = ApiResp.builder()
                .message("User registered successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
//            @RequestParam String refreshToken,
            @CookieValue("refreshToken") String refreshToken) {
        Logger.log("Refresh token: " + refreshToken);
        String newAccessToken = tokenProvider.refreshAccessToken(refreshToken);
        Logger.log("newAccessToken", newAccessToken);
        return ResponseEntity.ok(new TokenResponse(newAccessToken));
    }

    //logout, tạm thời để require = false vì đang deploy vercel
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletRequest req, HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        String accessToken = tokenProvider.getJwtFromRequest(req);
        tokenBlacklistService.addAccessTokenToBlacklist(accessToken);
//        tạm thời comment vì đang deploy vercel nó k cho set cookie ở bước login, còn local thì ok
//        tokenBlacklistService.addRefreshTokenToBlacklist(refreshToken);

        ApiResp<String> apiResponse = ApiResp.<String>builder().message("Logout successfully").build();
        return ResponseEntity.ok().body(apiResponse);
    }


    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request, HttpServletResponse response) throws GeneralSecurityException, IOException {
        // Xác minh idToken từ Google
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId)) // Thay bằng Client ID của bạn
                .build();
        GoogleIdToken idToken = verifier.verify(request.getIdToken());
        if (idToken == null)
            throw new GeneralSecurityException("Invalid ID token");
        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        return ResponseEntity.ok(authService.loginGoogle(email, name, pictureUrl,response));
    }

}