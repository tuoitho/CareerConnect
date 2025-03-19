package com.careerconnect.constant;

public class SecurityEndpoint {
    public static final String[] AUTH_WHITELIST = {
            "/api-docs/**",
            "/favicon.ico",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**",
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh-token",
            "/api/refresh/**",
            "/ws-chat/**",
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
//    public static final String[] CANDIDATE_AUTH = {
//        "/api/job/apply" ,
//        "/api/job/applied",
//        "/api/job/**",
//        "/api/candidate/profile/me",
//        "/api/candidate/profile/cv/**",
//        "/api/candidate/profile/chat/**",
//        "/api/chat/**",
//        "/chat.send",
//        "/chat.markAsDelivered",
//        "/chat.markAsRead"
//    };
//    public static final String[] RECRUITER_AUTH = {
//            "/api/application",
//            "/api/chat/**",
//            "/api/candidate/profile/{id}",
//            "/chat.send",
//            "/chat.markAsDelivered",
//            "/chat.markAsRead"
//
//    };
//    public static final String[] REQUIRED_AUTH = {
//            "/api/test"
//    };
}
