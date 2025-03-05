package com.careerconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "skill")
public class Skill {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long skillId;
    private String name;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Candidate candidate;
}
