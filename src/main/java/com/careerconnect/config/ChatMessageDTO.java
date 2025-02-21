package com.careerconnect.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class ChatMessageDTO {
    private Long senderId;
    private Long recipientId;
    private String content;
    private String type;
    // validation annotations 
}