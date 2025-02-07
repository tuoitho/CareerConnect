package com.careerconnect.service.impl;

import com.careerconnect.dto.request.CandidateProfileRequest;
import com.careerconnect.dto.response.CandidateProfileResponse;
import com.careerconnect.entity.*;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.repository.*;
import com.careerconnect.service.FileStoreService;
import com.careerconnect.service.ImageService;
import com.cloudinary.Cloudinary;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateProfileService {

    private final CandidateRepo candidateRepository;
    private final Cloudinary cloudinary;
    private final ImageService imageService;
    private final FileStoreService fileStoreService;

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
                        .active(cv.getActive())
                        .build())
                .collect(Collectors.toSet());
        return CandidateProfileResponse.builder()
                .userId(candidate.getUserId())
                .fullname(candidate.getFullname())
                .avatar(candidate.getAvatar())
                .phone(candidate.getPhone())
                .email(candidate.getEmail())
                .bio(candidate.getBio())
                .skills(candidate.getSkills().stream().map(Skill::getName).collect(Collectors.toSet()))
                .educations(educations)
                .experiences(experiences)
                .cvs(cvs)
                .build();
    }
    @Transactional
    public CandidateProfileResponse updateProfile(Long candidateId, CandidateProfileRequest request, MultipartFile avatar) {
        Candidate candidate = candidateRepository.findByIdWithRelations(candidateId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        BeanUtils.copyProperties(request, candidate, "skills","educations", "experiences", "cvs");
        Set<Skill> skills = request.getSkills().stream()
                .map(skill ->Skill.builder()
                        .skillId(null)
                        .name(skill)
                        .candidate(candidate)
                        .build())
                .collect(Collectors.toSet());
        if (candidate.getSkills() == null) {
            candidate.setSkills(new HashSet<>());
        }
        candidate.getSkills().clear();
        candidate.getSkills().addAll(skills);
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

        // Handle avatar upload if provided
        if (avatar != null && !avatar.isEmpty()) {
            String avatarPath = imageService.uploadCloudinary(avatar);
            candidate.setAvatar(avatarPath);
            candidate.setAvatar(avatarPath);
        }
        Candidate savedCandidate = candidateRepository.save(candidate);
        return CandidateProfileResponse.builder()
                .userId(savedCandidate.getUserId())
                .fullname(savedCandidate.getFullname())
                .avatar(savedCandidate.getAvatar())
                .phone(savedCandidate.getPhone())
                .email(savedCandidate.getEmail())
                .bio(savedCandidate.getBio())
                .skills(savedCandidate.getSkills().stream().map(Skill::getName).collect(Collectors.toSet()))
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

    public CandidateProfileResponse.CVResponse uploadCV(Long candidateId, String cvName, MultipartFile file) {
        Candidate candidate = candidateRepository.findByIdWithRelations(candidateId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String cvPath = fileStoreService.uploadPdf(file);
        CV cv = CV.builder()
                .name(cvName)
                .path(cvPath)
                .candidate(candidate)
                .build();
        candidate.addCV(cv);
        Candidate savedCandidate=candidateRepository.save(candidate);
        //get saved cv
        CV savedCV = savedCandidate.getCvs().stream()
                .filter(c -> c.getPath().equals(cvPath))
                .findFirst().get();
        return CandidateProfileResponse.CVResponse.builder()
                .cvId(savedCV.getCvId())
                .name(savedCV.getName())
                .path(savedCV.getPath())
                .active(savedCV.getActive())
                .build();
    }

    public CandidateProfileResponse.CVResponse deleteCV(Long candidateId, Long cvId) {
        Candidate candidate = candidateRepository.findByIdWithRelations(candidateId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        CV cv = candidate.getCvs().stream()
                .filter(c -> c.getCvId().equals(cvId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CV_NOT_FOUND));
        candidate.removeCV(cv);
        candidateRepository.save(candidate);
        fileStoreService.deleteFile(cv.getPath());
        return CandidateProfileResponse.CVResponse.builder()
                .cvId(cv.getCvId())
                .name(cv.getName())
                .path(cv.getPath())
                .active(cv.getActive())
                .build();
    }
}
