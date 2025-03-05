package com.careerconnect.atest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    List<SavedJob> findByCandidateUserId(Long candidateId);
    Optional<SavedJob> findByCandidateUserIdAndJobJobId(Long candidateId, Long jobId);
    void deleteByCandidateUserIdAndJobJobId(Long candidateId, Long jobId);
    boolean existsByCandidate_userIdAndJob_jobId(Long candidateId, Long jobId);
}