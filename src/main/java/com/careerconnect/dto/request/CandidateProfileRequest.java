package com.careerconnect.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CandidateProfileRequest {
    @NotBlank
    private String fullname;
    private String avatar;
    
    @Pattern(regexp = "\\d{10}")
    private String phone;
    
    @Email
    private String email;
    
    private String bio;
    private List<String> skills;
    
    @Valid
    private List<EducationRequest> educations;
    
    @Valid
    private List<ExperienceRequest> experiences;
    
    @Valid
    private List<CVRequest> cvs;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EducationRequest {
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

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienceRequest {
        private Long experienceId;
        private String companyName;
        private String position;
        private String startDate;
        private String endDate;
        private String description;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CVRequest {
        private Long cvId;
        private String name;
        private String path;
        private Boolean active;
    }
}