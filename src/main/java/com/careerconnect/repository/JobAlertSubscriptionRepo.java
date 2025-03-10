package com.careerconnect.repository;

import com.careerconnect.entity.JobAlertSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobAlertSubscriptionRepo extends JpaRepository<JobAlertSubscription, Long> {
    List<JobAlertSubscription> findByKeywordContainingIgnoreCaseAndActiveTrue(String keyword);
    List<JobAlertSubscription> findByCandidate_UserIdAndActiveTrue(Long candidateId);

    @Query("SELECT distinct s FROM JobAlertSubscription s WHERE :content LIKE CONCAT('%', s.keyword, '%')")
    List<JobAlertSubscription> findMatchingSubscriptions(@Param("content") String content);
}