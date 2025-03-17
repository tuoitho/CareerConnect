package com.careerconnect.repository;

import com.careerconnect.entity.Application;
import com.careerconnect.entity.Candidate;
import com.careerconnect.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepo extends JpaRepository<Application, Long> {
    boolean existsByJob(Job job);

    List<Application> findAllByJob(Job job);

    boolean existsByCandidateAndJob(Candidate candidate, Job job);

    Page<Application> findAllByCandidate_userId(Long candidateId, Pageable pageable);

    boolean existsByCandidate_userIdAndJob_jobId(Long candidateId, Long id);

    Long countApplicationByJob_JobId(Long jobId);
}
