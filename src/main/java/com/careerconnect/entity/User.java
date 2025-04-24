package com.careerconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "[user]")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long userId;
    protected String username;
    protected String password;
    protected String email;
    protected String fullname;
    protected Boolean active=true;

    private LocalDateTime createdAt = LocalDateTime.now();


    @Column(name = "coin_balance", nullable = false)
    private Integer coinBalance = 0; // Số xu hiện tại, mặc định là 0

    @ManyToOne
    @JoinColumn
    protected Role role;
}
