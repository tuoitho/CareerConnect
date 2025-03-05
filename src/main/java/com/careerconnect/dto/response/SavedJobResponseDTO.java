package com.careerconnect.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SavedJobResponseDTO {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String companyLogo;
    private LocalDateTime savedAt;
}