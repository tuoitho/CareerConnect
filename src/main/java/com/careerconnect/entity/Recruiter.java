package com.careerconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
//@Builder
public class Recruiter extends User{
    private String fullname;
    private String contact;
    private String email;
    private boolean isRepresentative; // Có phải người đại diện không

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Company company;

    @OneToMany(mappedBy = "inviter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invitation> sentInvitations = new ArrayList<>();

    @OneToMany(mappedBy = "invitee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invitation> receivedInvitations = new ArrayList<>();

}
