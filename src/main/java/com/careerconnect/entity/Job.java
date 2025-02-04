package com.careerconnect.entity;

import com.careerconnect.enums.ExpEnum;
import com.careerconnect.enums.JobTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;
    private String title;
    private String description;
    private String location;
    @Enumerated(EnumType.STRING)
    private JobTypeEnum type;
//    range of salary
    private String minSalary;
    private String maxSalary;

    private LocalDateTime created;
    private LocalDateTime updated;
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private ExpEnum experience;

    private String category;

    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Company company;

    @OneToMany(mappedBy = "job")
    private List<Application> applications;
}
