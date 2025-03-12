// src/main/java/com/careerconnect/service/impl/ApplicationService.java
package com.careerconnect.service.impl;

import com.careerconnect.dto.response.ApplicationDetailResponse;
import com.careerconnect.entity.Application;
import com.careerconnect.entity.Recruiter;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.ApplicationRepo;
import com.careerconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepo applicationRepo;
    private final UserRepository userRepository;

    public ApplicationDetailResponse getApplicationDetail(Long recruiterId, Long applicationId) {
        // Verify recruiter exists
        Recruiter recruiter = (Recruiter) userRepository.findById(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, recruiterId));

        // Fetch application
        Application application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException(Application.class, applicationId));

        // Verify if the job belongs to the recruiter's company
        if (!application.getJob().getCompany().equals(recruiter.getCompany())) {
            throw new AppException(ErrorCode.NOT_PERMITTED);
        }

        return ApplicationDetailResponse.builder()
                .applicationId(application.getApplicationId())
                .jobId(application.getJob().getJobId())
                .jobTitle(application.getJob().getTitle())
                .candidateId(application.getCandidate().getUserId())
                .candidateName(application.getCandidate().getFullname())
                .candidateAvatar(application.getCandidate().getAvatar())
                .appliedAt(application.getAppliedAt())
                .processed(application.isProcessed())
                .coverLetter(application.getCoverLetter())
                .cvPath(application.getApplicationCV().getPath())
                .build();
    }
}