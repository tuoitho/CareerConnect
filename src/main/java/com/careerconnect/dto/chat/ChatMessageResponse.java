package com.careerconnect.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
    private Long id;

    private Long senderId;


    private Long recipientId;

    private String content;
    private LocalDateTime timestamp;
    private String status; // SENT, DELIVERED, READ
}
