package com.careerconnect.repository;

import com.careerconnect.entity.InterviewRoom;
import com.careerconnect.enums.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InterviewRoomRepository extends JpaRepository<InterviewRoom, UUID> {
    
    List<InterviewRoom> findByRecruiterId(Long recruiterId);
    
    List<InterviewRoom> findByCandidateId(Long candidateId);
    
    List<InterviewRoom> findByApplicationId(Long applicationId);
    
    List<InterviewRoom> findByScheduledTimeBetween(LocalDateTime start, LocalDateTime end);
    
    List<InterviewRoom> findByStatusAndScheduledTimeBefore(InterviewStatus status, LocalDateTime time);
    
    Optional<InterviewRoom> findByIdAndRecruiterId(UUID id, Long recruiterId);
    
    Optional<InterviewRoom> findByIdAndCandidateId(UUID id, Long candidateId);
}