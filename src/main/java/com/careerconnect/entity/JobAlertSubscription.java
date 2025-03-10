package com.careerconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_alert_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobAlertSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Candidate candidate;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false)
    @Builder.Default
    private String notificationMethod = "EMAIL";

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}