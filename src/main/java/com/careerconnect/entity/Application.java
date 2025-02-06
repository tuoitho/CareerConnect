package com.careerconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    private String coverLetter;

    @Builder.Default
    private LocalDateTime appliedAt=LocalDateTime.now();

    @Builder.Default
    private boolean processed=false;

    @OneToOne(mappedBy = "application")
    private ApplicationCV applicationCV;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonIgnore
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonIgnore
    private Candidate candidate;


}
