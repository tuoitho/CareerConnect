package com.careerconnect.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignalRequest {
    
    private UUID interviewId;
    private String type; // "offer", "answer", "ice-candidate", "chat"
    private Object data; // The actual signal data (SDP, ICE candidate, message, etc.)
}