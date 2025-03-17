package com.careerconnect.entity;

import jakarta.persistence.*;
import lombok.*;

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
    protected Boolean active=true;

    @Column(name = "coin_balance", nullable = false)
    private Integer coinBalance = 0; // Số xu hiện tại, mặc định là 0

    @ManyToOne
    @JoinColumn
    protected Role role;
}
