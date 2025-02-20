package com.careerconnect.dto.response;

import com.careerconnect.entity.ApplicationCV;
import com.careerconnect.entity.Candidate;
import com.careerconnect.entity.Job;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppliedJobResponse {
    private Long applicationId;
    private String coverLetter;
    private LocalDateTime appliedAt;
    private AppliedJob job;

//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Data
//    @Builder
//    public static class AppliedCV {
//        private Long applicationCVId;
//        private String name;
//        private String path;
//    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AppliedJob {
        private Long jobId;
        private String title;
        private String image;
    }

}
