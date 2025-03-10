package com.careerconnect.service.impl;

import com.careerconnect.dto.request.JobAlertSubscriptionRequest;
import com.careerconnect.dto.response.JobAlertSubscriptionResponse;
import com.careerconnect.entity.Candidate;
import com.careerconnect.entity.JobAlertSubscription;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.CandidateRepo;
import com.careerconnect.repository.JobAlertSubscriptionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobAlertSubscriptionService {
    private final JobAlertSubscriptionRepo jobAlertSubscriptionRepo;
    private final CandidateRepo candidateRepo;

    public void subscribe(Long candidateId, JobAlertSubscriptionRequest request) {
        Candidate candidate = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException(Candidate.class, candidateId));

        JobAlertSubscription subscription = JobAlertSubscription.builder()
                .candidate(candidate)
                .keyword(request.getKeyword())
                .active(true)
                .build();

        jobAlertSubscriptionRepo.save(subscription);
    }

    public void unsubscribe(Long candidateId, Long subscriptionId) {
        JobAlertSubscription subscription = jobAlertSubscriptionRepo.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException(JobAlertSubscription.class, subscriptionId));
        if (!subscription.getCandidate().getUserId().equals(candidateId)) {
            throw new RuntimeException("Not authorized to unsubscribe");
        }
        subscription.setActive(false);
        jobAlertSubscriptionRepo.save(subscription);
    }

    public List<JobAlertSubscriptionResponse> getJobAlertSubscriptions(Long candidateId) {
        return jobAlertSubscriptionRepo.findByCandidate_UserIdAndActiveTrue(candidateId).stream()
                .map(subscription -> JobAlertSubscriptionResponse.builder()
                        .id(subscription.getId())
                        .keyword(subscription.getKeyword())
                        .active(subscription.isActive())
                        .build())
                .toList();
    }
}