package com.careerconnect.atest;

import com.careerconnect.dto.request.LoginRequest;
import com.careerconnect.dto.response.LoginResponse;
import com.careerconnect.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestControllerrrrrrrr {
    private final AuthService authService;

    public TestControllerrrrrrrr(AuthService authService) {
        this.authService = authService;
    }
//    private final JobService2 jobService2;
//
//    public TestControllerrrrrrrr(JobService2 jobService2) {
//        this.jobService2 = jobService2;
//    }

//    @GetMapping("/api/tttt")
//    public String test() {
////        jobService2.createJob();
//        return "OK";
//    }

    @PostMapping("/api/tttt")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest , HttpServletResponse response) {
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
}
