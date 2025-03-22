package com.careerconnect.atest.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityLogResponse {
    private Long id;
    private String description;
    private String timestamp;
    private String userEmail;
}