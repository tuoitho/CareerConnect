// src/main/java/com/careerconnect/dto/response/AdminJobResponse.java
package com.careerconnect.atest.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminJobResponse {
    private Long id;
    private String title;
    private String companyName;
    private boolean isApproved;
    private boolean isVisible;
    private int reportCount;
    private String createdAt;
}