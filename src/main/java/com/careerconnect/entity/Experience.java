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
public class Experience {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long experienceId;
    private String companyName;
    private String position;
    private String startDate;
    private String endDate;
    private String description;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Candidate candidate;
}
