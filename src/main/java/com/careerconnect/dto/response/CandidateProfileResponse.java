package com.careerconnect.dto.response;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfileResponse {
    private Long userId;
    private String fullname;
    private String avatar;
    private String phone;
    private String email;
    private String bio;
    private Set<String> skills;
    private Set<EducationResponse> educations;
    private Set<ExperienceResponse> experiences;
    private Set<CVResponse> cvs;


    // Các DTO response lồng
    @Getter @Setter
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

    @Getter @Setter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CVResponse {
        private Long cvId;
        private String name;
        private String path;
        private Boolean active;
    }
}