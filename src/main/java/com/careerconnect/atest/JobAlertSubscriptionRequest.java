package com.careerconnect.atest;

import lombok.Data;

@Data
public class JobAlertSubscriptionRequest {
    private String keyword;
    private String notificationMethod; // "EMAIL", "WEBSOCKET", "BOTH"
}