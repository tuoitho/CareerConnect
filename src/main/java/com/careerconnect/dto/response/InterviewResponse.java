package com.careerconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResponse {
    
    private UUID id;
    private Long applicationId;
    private Long candidateId;
    private String candidateName;
    private Long recruiterId;
    private String recruiterName;
    private Long jobId;
    private String jobTitle;
    private LocalDateTime scheduledTime;
    private String status;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}