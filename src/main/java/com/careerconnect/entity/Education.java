package com.careerconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Education {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long educationId;
    private String school;
    private String major;
    private String degree;
    private String startDate;
    private String endDate;
    private String description;
    private String gpa;
    private String type;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Candidate candidate;
}
