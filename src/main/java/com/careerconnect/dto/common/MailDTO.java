package com.careerconnect.dto.common;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailDTO {
    private String from;
    private String to;
    private String subject;
    private String text;
}