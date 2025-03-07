package com.careerconnect.atest;

import com.careerconnect.entity.Candidate;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.CandidateRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .notificationMethod(request.getNotificationMethod())
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
}