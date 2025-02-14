package com.careerconnect.dto.response;

import com.careerconnect.enums.ExpEnum;
import com.careerconnect.enums.JobTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDetailResponse {
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
    private boolean applied;
}
