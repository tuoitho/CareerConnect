package com.careerconnect.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserChatResponse {
    private Long id;
    private String name;
    private String fullname;
    private String avatar;
    private boolean active;
}