// src/main/java/com/careerconnect/dto/response/CandidateDetailResponse.java
package com.careerconnect.dto.response;

import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class CandidateDetailResponse {
    private Long candidateId;
    private String fullname;
    private String avatar;
    private String phone;
    private String email;
    private String bio;
    private Set<String> skills;
    private Set<CandidateDetailResponse.EducationResponse> educations;
    private Set<CandidateDetailResponse.ExperienceResponse> experiences;

    // Các DTO response lồng
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static  class EducationResponse {
        private Long educationId;
        private String school;
        private String major;
        private String degree;
        private String startDate;
        private String endDate;
        private String description;
        private String gpa;
        private String type;
    }

    @Getter @Setter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienceResponse {
        private Long experienceId;
        private String companyName;
        private String position;
        private String startDate;
        private String endDate;
        private String description;
    }

}