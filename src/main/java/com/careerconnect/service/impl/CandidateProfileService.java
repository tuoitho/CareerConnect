package com.careerconnect.service.impl;

import com.careerconnect.dto.response.SimpleCandidateResponse;
import com.careerconnect.dto.request.CandidateProfileRequest;
import com.careerconnect.dto.response.CandidateDetailResponse;
import com.careerconnect.dto.response.CandidateProfileResponse;
import com.careerconnect.entity.*;
import com.careerconnect.exception.ResourceNotFoundException;
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
    private final CvRepo cvRepo;

    @Transactional
    public CandidateProfileResponse getProfile(Long candidateId) {
        Candidate candidate = candidateRepository.findByIdWithRelations(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException(Candidate.class, candidateId));
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
                .orElseThrow(() -> new ResourceNotFoundException(Candidate.class, candidateId));

        BeanUtils.copyProperties(request, candidate, "skills", "educations", "experiences", "cvs");
        Set<Skill> skills = request.getSkills().stream()
                .map(skill -> Skill.builder()
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
    @Transactional
    public CandidateProfileResponse.CVResponse uploadCV(Long candidateId, String cvName, MultipartFile file) {
        Candidate candidate = candidateRepository.findByIdWithRelations(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException(Candidate.class, candidateId));
        String cvPath = fileStoreService.uploadPdf(file);
        CV cv = CV.builder()
                .name(cvName)
                .path(cvPath)
                .candidate(candidate)
                .build();
        candidate.addCV(cv);
        Candidate savedCandidate = candidateRepository.save(candidate);
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
    @Transactional
    public CandidateProfileResponse.CVResponse deleteCV(Long candidateId, Long cvId) {
        Candidate candidate = candidateRepository.findByIdWithRelations(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException(Candidate.class, candidateId));
        CV cv = candidate.getCvs().stream()
                .filter(c -> c.getCvId().equals(cvId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(CV.class, cvId));
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

    public Set<CandidateProfileResponse.CVResponse> getCVs(Long candidateId) {
        Set<CV> cvs = cvRepo.findByCandidate_UserId(candidateId).orElseThrow(() -> new ResourceNotFoundException(Candidate.class, candidateId));
        return cvs.stream()
                .map(cv -> CandidateProfileResponse.CVResponse.builder()
                        .cvId(cv.getCvId())
                        .name(cv.getName())
                        .path(cv.getPath())
                        .active(cv.getActive())
                        .build())
                .collect(Collectors.toSet());
    }


    public CandidateDetailResponse getCandidateDetail(Long candidateId) {
        Candidate candidate = (Candidate) candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException(Candidate.class, candidateId));

        // Ánh xạ Education
        Set<CandidateDetailResponse.EducationResponse> educationResponses = candidate.getEducations()
                .stream()
                .map(edu -> CandidateDetailResponse.EducationResponse.builder()
                        .educationId(edu.getEducationId())
                        .school(edu.getSchool())
                        .major(edu.getMajor())
                        .degree(edu.getDegree())
                        .startDate(edu.getStartDate())
                        .endDate(edu.getEndDate())
                        .description(edu.getDescription())
                        .gpa(edu.getGpa())
                        .type(edu.getType())
                        .build())
                .collect(Collectors.toSet());

        // Ánh xạ Experience
        Set<CandidateDetailResponse.ExperienceResponse> experienceResponses = candidate.getExperiences()
                .stream()
                .map(exp -> CandidateDetailResponse.ExperienceResponse.builder()
                        .experienceId(exp.getExperienceId())
                        .companyName(exp.getCompanyName())
                        .position(exp.getPosition())
                        .startDate(exp.getStartDate())
                        .endDate(exp.getEndDate())
                        .description(exp.getDescription())
                        .build())
                .collect(Collectors.toSet());

        return CandidateDetailResponse.builder()
                .candidateId(candidate.getUserId())
                .fullname(candidate.getFullname())
                .avatar(candidate.getAvatar())
                .phone(candidate.getPhone())
                .email(candidate.getEmail())
                .bio(candidate.getBio())
                .skills(candidate.getSkills().stream().map(Skill::getName).collect(Collectors.toSet()))
                .educations(educationResponses)
                .experiences(experienceResponses)
                .build();

    }

    public SimpleCandidateResponse getCandidateForChat(Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException(Candidate.class, candidateId));
        return SimpleCandidateResponse.builder()
                .id(candidate.getUserId())
                .name(candidate.getFullname())
                .avatar(candidate.getAvatar())
                .build();

    }
}
