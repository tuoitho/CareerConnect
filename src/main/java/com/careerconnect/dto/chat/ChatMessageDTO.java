package com.careerconnect.dto.chat;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ChatMessageDTO {
    private Long id;
    private Long senderId;
    private Long recipientId;
    private String content;
    private String type;
    private Long tempId;
    private String status;
    // validation annotations 
}