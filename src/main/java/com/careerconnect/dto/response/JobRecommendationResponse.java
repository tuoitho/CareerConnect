package com.careerconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRecommendationResponse {
    private Long jobId;
    private String title;
    private String company;
    private String location;
    private int minSalary;
    private int maxSalary;
    private int matchScore;
}