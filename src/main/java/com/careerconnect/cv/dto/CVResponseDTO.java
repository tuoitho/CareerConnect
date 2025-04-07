package com.careerconnect.cv.dto;

import lombok.Data;

@Data
public class CVResponseDTO {
    private Long id;
    private String name;
    private String content;
    private String templateId;
}