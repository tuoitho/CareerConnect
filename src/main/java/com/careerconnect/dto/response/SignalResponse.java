package com.careerconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignalResponse {
    
    private UUID interviewId;
    private String type; // "offer", "answer", "ice-candidate", "chat"
    private Long senderId;
    private String senderName; // Optional, mainly used for chat messages
    private Object data; // The actual signal data (SDP, ICE candidate, message, etc.)
}