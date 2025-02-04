package com.careerconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.* ;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ApplicationCV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationCVId;

    private String name;
    private String path;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonIgnore
    private Application application;
}
