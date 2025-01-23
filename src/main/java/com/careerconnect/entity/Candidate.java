package com.careerconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Candidate extends User{
    private String fullname;
    private String avatar;
    private String phone;
    private String bio;

//    @ElementCollection
//    @CollectionTable
//    private List<String> skills;

//    @ElementCollection
//    @CollectionTable
//    private List<String> languages;

//    @ElementCollection
//    @CollectionTable
//    private List<String> hobbies;

//    @OneToMany(mappedBy = "candidate")
//    private List<Education> educations;

//    @OneToMany(mappedBy = "candidate")
//    private List<Experience> experiences;
//
//    @OneToMany(mappedBy = "candidate")
//    private List<CV> cvs;
//
//    @OneToMany(mappedBy = "candidate")
//    private List<Application> applications;
}
