//package com.careerconnect.security;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.stream.Collectors;
//
//@Component
//public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) throws IOException, ServletException {
//        response.setStatus(HttpServletResponse.SC_OK);
//        //tra ve json
//        response.setContentType("application/json");
//        response.getWriter().write("{\"message\": \"Login successful\"}");
//        response.getWriter().flush();
//
//
//    }
//}