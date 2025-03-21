package com.careerconnect.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private String time;
    private boolean isRead;
    private String type;
}