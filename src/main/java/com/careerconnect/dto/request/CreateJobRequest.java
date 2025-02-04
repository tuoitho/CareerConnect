package com.careerconnect.dto.request;

import com.careerconnect.enums.ExpEnum;
import com.careerconnect.enums.JobTypeEnum;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateJobRequest {
    private String title;
    private String description;
    private String location;
    private String type;
    private String minSalary;
    private String maxSalary;
    private LocalDateTime deadline;
    private ExpEnum experience;
    private String category;
    private boolean active;
}