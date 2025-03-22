// src/main/java/com/careerconnect/dto/response/AdminCompanyResponse.java
package com.careerconnect.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminCompanyResponse {
    private Long id;
    private String name;
    private String email;
    private String logo;
    private boolean isApproved;
    private boolean isLocked;
    private String createdAt;
}