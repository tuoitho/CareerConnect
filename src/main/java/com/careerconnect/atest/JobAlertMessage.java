package com.careerconnect.atest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobAlertMessage {
    private Long candidateId;
    private String email;
    private String keyword;
    private String jobTitle;
    private String jobDescription;
    private String jobLocation;
    private Long jobId;
    private String notificationMethod; // "EMAIL", "WEBSOCKET", "BOTH"
}