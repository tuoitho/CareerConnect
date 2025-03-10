package com.careerconnect.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationMessage {
    private Long jobId;
    private String jobTitle;
    private Long candidateId;
}