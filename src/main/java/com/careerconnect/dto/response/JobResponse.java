package com.careerconnect.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobResponse {
    private Long jobId;
    private String title;
    private String description;
    private String location;
    private String jobType;
    private String experience;
    private String minSalary;
    private String maxSalary;
    private LocalDateTime created;
    private LocalDateTime deadline;
    private String category;
    private Boolean active;
    private String companyName;
    private String companyLogo;
    private String area;
}