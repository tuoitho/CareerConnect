package com.careerconnect.dto.other;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobAlertMessage implements Serializable {
    private Long candidateId;
    private String email;
    private String keyword;
    private String jobTitle;
    private String jobDescription;
    private String jobLocation;
    private Long jobId;
    private String notificationMethod; // "EMAIL", "WEBSOCKET", "BOTH"
}