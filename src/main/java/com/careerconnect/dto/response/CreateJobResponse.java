package com.careerconnect.dto.response;

import com.careerconnect.enums.ExpEnum;
import com.careerconnect.enums.JobTypeEnum;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateJobResponse {
    private Long jobId;
    private String title;
    private String description;
    private String location;
    private JobTypeEnum type;
    private String minSalary;
    private String maxSalary;
    private LocalDateTime created;
    private LocalDateTime updated;
    private LocalDateTime deadline;
    private ExpEnum experience;
    private String category;
    private boolean active;
    private Long companyId;
}
