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
@Table(name = "cv")
public class CV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cvId;
    private String name;
    private String path;
    @Builder.Default
    private Boolean active=true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonIgnore
    private Candidate candidate;

}
