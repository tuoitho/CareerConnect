package com.careerconnect.model.interview;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interview_rooms")
@Builder
public class InterviewRoom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String roomName;
    
    @Column(nullable = false)
    private Long recruiterId;
    
    @Column(nullable = false)
    private Long candidateId;
    
    @Column(nullable = true)
    private Long applicationId;
    
    @Column(nullable = false)
    private LocalDateTime scheduledTime;
    
    @Column(nullable = true)
    private LocalDateTime startTime;
    
    @Column(nullable = true)
    private LocalDateTime endTime;
    
    @Column(nullable = false)
    private InterviewStatus status =InterviewStatus.SCHEDULED;
    
    @Column(nullable = true, length = 500)
    private String notes;
    
    @Column(nullable = true)
    private String recordingUrl;
    
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}