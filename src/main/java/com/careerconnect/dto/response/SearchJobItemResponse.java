package com.careerconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchJobItemResponse {
    private Long jobId;
    private String title;
    private String location;
    private String minSalary;
    private String maxSalary;
    private String companyName;
    private String companyLogo;
    private boolean applied;
}
