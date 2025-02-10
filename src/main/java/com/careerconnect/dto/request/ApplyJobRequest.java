package com.careerconnect.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyJobRequest {
    private Long jobId;
    private String coverLetter;
    private Long cvId;
}
