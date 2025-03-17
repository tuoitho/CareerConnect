package com.careerconnect.constant;

public class SecurityEndpoint {
    public static final String[] AUTH_WHITELIST = {
            "/api-docs/**",
            "/favicon.ico",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**",
            "/api/auth/**",
            "/api/refresh/**",
            "/ws-chat/**",
            "/api/test2",
            "/api/vnpay/payment-return",
//            "/api/vnpay/**",
    };
    public static final String[] REQUIRED_AUTH = {
            "/api/test"
    };
}
