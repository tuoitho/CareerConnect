package com.careerconnect.dto.chat;

import lombok.Data;

@Data
public class MarkAsReadRequest {
    private Long messageId;
}