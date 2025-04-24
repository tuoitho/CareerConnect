package com.careerconnect.constant;

public class SecurityEndpoint {
    public static final String[] AUTH_WHITELIST = {
            "/test",
            "/api/tttt",
            "/api-docs/**",
            "/favicon.ico",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**",
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh-token",
            "/api/auth/google",
            "/api/refresh/**",
            "/ws-chat/**",
            "/ws-interview/**",
            "/api/test2",
            "/api/vnpay/payment-return",
            "/api/company/jobs/search",
            "/chat.send",
        "/chat.markAsDelivered",
        "/chat.markAsRead",
//            "/api/vnpay/**",
    };
    public static final String CANDIDATE = "hasRole('CANDIDATE')";
    public static final String RECRUITER = "hasRole('RECRUITER')";
    public static final String BOTH = "hasAnyRole('CANDIDATE','RECRUITER')";
    public static final String ADMIN = "hasRole('ADMIN')";

}
