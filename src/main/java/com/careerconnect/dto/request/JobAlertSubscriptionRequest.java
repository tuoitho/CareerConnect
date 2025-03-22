package com.careerconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobAlertSubscriptionRequest {
    @NotBlank(message = "Keyword không được để trống")
    private String keyword;
//    private String notificationMethod;
}