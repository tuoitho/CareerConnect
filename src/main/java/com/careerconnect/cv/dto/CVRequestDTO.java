package com.careerconnect.cv.dto;

import lombok.Data;

@Data
public class CVRequestDTO {
    private String name;
    private String content;
    private String templateId;
}