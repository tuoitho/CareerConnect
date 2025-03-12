// src/main/java/com/careerconnect/dto/response/ApplicationDetailResponse.java
package com.careerconnect.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationDetailResponse {
    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private Long candidateId;
    private String candidateName;
    private String candidateAvatar;
    private LocalDateTime appliedAt;
    private boolean processed;
    private String coverLetter;
    private String cvPath;
}