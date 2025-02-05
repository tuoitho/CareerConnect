package com.careerconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private LoggedInUser user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoggedInUser {
        private Long userId;
        private String username;
        private String role;
    }

}
