package com.careerconnect.config;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Builder
public class ChatMessageDTO {
    private Long id;
    private Long senderId;
    private Long recipientId;
    private String content;
    private String type;
    private Long tempId;

    // validation annotations 
}