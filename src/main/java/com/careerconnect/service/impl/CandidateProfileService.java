package com.careerconnect.service.impl;

import com.careerconnect.dto.request.CandidateProfileRequest;
import com.careerconnect.dto.request.RecruiterProfileRequest;
import com.careerconnect.dto.response.CandidateProfileResponse;
import com.careerconnect.dto.response.RecruiterProfileResponse;
import com.careerconnect.entity.*;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.mapper.CompanyMapper;
import com.careerconnect.mapper.InvitationMapper;
import com.careerconnect.repository.*;
import com.careerconnect.service.ImageService;
import com.careerconnect.service.MailService;
import com.careerconnect.service.PaginationService;
import com.careerconnect.util.AuthenticationHelper;
import com.careerconnect.util.Logger;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateProfileService {

    private final CandidateRepo candidateRepository;
    @Transactional
    public CandidateProfileResponse getProfile(Long candidateId) {
        Candidate candidate = candidateRepository.findByIdWithRelations(candidateId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Set<CandidateProfileResponse.EducationResponse> educations = candidate.getEducations().stream()
                .map(edu -> CandidateProfileResponse.EducationResponse.builder()
                        .school(edu.getSchool())
                        .major(edu.getMajor())
                        .educationId(edu.getEducationId())
                        .type(edu.getType())
                        .startDate(edu.getStartDate())
                        .endDate(edu.getEndDate())
                        .gpa(edu.getGpa())
                        .degree(edu.getDegree())
                        .description(edu.getDescription())
                        .build())
                .collect(Collectors.toSet());
        Set<CandidateProfileResponse.ExperienceResponse> experiences = candidate.getExperiences().stream()
                .map(exp -> CandidateProfileResponse.ExperienceResponse.builder()
                        .companyName(exp.getCompanyName())
                        .position(exp.getPosition())
                        .experienceId(exp.getExperienceId())
                        .startDate(exp.getStartDate())
                        .endDate(exp.getEndDate())
                        .description(exp.getDescription())
                        .build())
                .collect(Collectors.toSet());
        Set<CandidateProfileResponse.CVResponse> cvs = candidate.getCvs().stream()
                .map(cv -> CandidateProfileResponse.CVResponse.builder()
                        .cvId(cv.getCvId())
                        .name(cv.getName())
                        .path(cv.getPath())
                        .build())
                .collect(Collectors.toSet());
        return CandidateProfileResponse.builder()
                .userId(candidate.getUserId())
                .fullname(candidate.getFullname())
                .avatar(candidate.getAvatar())
                .phone(candidate.getPhone())
                .email(candidate.getEmail())
                .bio(candidate.getBio())
                .skills(candidate.getSkills())
                .educations(educations)
                .experiences(experiences)
                .cvs(cvs)
                .build();
    }
    @Transactional
    public CandidateProfileResponse updateProfile(Long candidateId, CandidateProfileRequest request) {
        Candidate candidate = candidateRepository.findByIdWithRelations(candidateId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        BeanUtils.copyProperties(request, candidate, "educations", "experiences", "cvs");
        Set<Education> educations = request.getEducations().stream().map(edu -> Education.builder()
                        .educationId(edu.getEducationId())
                        .school(edu.getSchool())
                        .major(edu.getMajor())
                        .type(edu.getType())
                        .startDate(edu.getStartDate())
                        .endDate(edu.getEndDate())
                        .gpa(edu.getGpa())
                        .degree(edu.getDegree())
                        .description(edu.getDescription())
                        .build())
                .collect(Collectors.toSet());
        candidate.assignEducations(educations);
        Set<Experience> experiences = request.getExperiences().stream()
                .map(exp -> Experience.builder()
                        .experienceId(exp.getExperienceId())
                        .companyName(exp.getCompanyName())
                        .position(exp.getPosition())
                        .startDate(exp.getStartDate())
                        .endDate(exp.getEndDate())
                        .description(exp.getDescription())
                        .build())
                .collect(Collectors.toSet());
        candidate.assignExperiences(experiences);
        Set<CV> cvs = request.getCvs().stream()
                .map(cv -> CV.builder()
                        .cvId(cv.getCvId())
                        .name(cv.getName())
                        .path(cv.getPath())
                        .build())
                .collect(Collectors.toSet());
        candidate.assignCVs(cvs);
        Candidate savedCandidate = candidateRepository.save(candidate);
        return CandidateProfileResponse.builder()
                .userId(savedCandidate.getUserId())
                .fullname(savedCandidate.getFullname())
                .avatar(savedCandidate.getAvatar())
                .phone(savedCandidate.getPhone())
                .email(savedCandidate.getEmail())
                .bio(savedCandidate.getBio())
                .skills(savedCandidate.getSkills())
                .educations(savedCandidate.getEducations().stream()
                        .map(edu -> CandidateProfileResponse.EducationResponse.builder()
                                .school(edu.getSchool())
                                .major(edu.getMajor())
                                .educationId(edu.getEducationId())
                                .type(edu.getType())
                                .startDate(edu.getStartDate())
                                .endDate(edu.getEndDate())
                                .gpa(edu.getGpa())
                                .degree(edu.getDegree())
                                .description(edu.getDescription())
                                .build())
                        .collect(Collectors.toSet()))
                .experiences(savedCandidate.getExperiences().stream()
                        .map(exp -> CandidateProfileResponse.ExperienceResponse.builder()
                                .companyName(exp.getCompanyName())
                                .position(exp.getPosition())
                                .experienceId(exp.getExperienceId())
                                .startDate(exp.getStartDate())
                                .endDate(exp.getEndDate())
                                .description(exp.getDescription())
                                .build())
                        .collect(Collectors.toSet()))
                .cvs(savedCandidate.getCvs().stream()
                        .map(cv -> CandidateProfileResponse.CVResponse.builder()
                                .cvId(cv.getCvId())
                                .name(cv.getName())
                                .path(cv.getPath())
                                .active(cv.getActive())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }
}
