// src/main/java/com/careerconnect/dto/response/AdminUserResponse.java
package com.careerconnect.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private boolean isLocked;
    private String createdAt;
}