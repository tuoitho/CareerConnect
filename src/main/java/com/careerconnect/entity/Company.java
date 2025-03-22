package com.careerconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;
    private String name;
    private String address; // Địa chỉ
    private String website;
    @Column(columnDefinition = "TEXT")
    private String description; // Mô tả
    @Column(columnDefinition = "TEXT")
    private String logo;
    @Builder.Default
    private Boolean active=true;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Recruiter> recruiters = new ArrayList<>();

    //add
    private boolean approved = false;
    private LocalDateTime createdAt = LocalDateTime.now();

}
