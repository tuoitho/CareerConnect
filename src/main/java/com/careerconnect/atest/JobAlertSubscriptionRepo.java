package com.careerconnect.atest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobAlertSubscriptionRepo extends JpaRepository<JobAlertSubscription, Long> {
    List<JobAlertSubscription> findByKeywordContainingIgnoreCaseAndActiveTrue(String keyword);
    List<JobAlertSubscription> findByCandidate_UserIdAndActiveTrue(Long candidateId);
}