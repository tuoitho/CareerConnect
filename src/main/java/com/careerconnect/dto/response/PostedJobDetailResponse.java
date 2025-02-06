package com.careerconnect.dto.response;

import com.careerconnect.entity.ApplicationCV;
import com.careerconnect.enums.ExpEnum;
import com.careerconnect.enums.JobTypeEnum;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostedJobDetailResponse {
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

    private List<ApplicationWithCandidate> applications;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApplicationWithCandidate {
        private Long applicationId;
        private String candidateName;
        private LocalDateTime appliedAt;
        private boolean processed;
    }
}
