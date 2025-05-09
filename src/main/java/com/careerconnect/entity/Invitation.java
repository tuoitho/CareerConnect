package com.careerconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "invitation")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token; // Token để xác thực lời mời
    private String email; // Email của người được mời

    private LocalDateTime expiryDate; // Thời hạn lời mời
    private boolean accepted; // Trạng thái chấp nhận lời mời

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private Recruiter inviter; // Người gửi lời mời

    @ManyToOne
    @JoinColumn()
    @JsonIgnore
    private Recruiter invitee; // Người được mời (nếu đã đăng ký)

    @ManyToOne
    @JoinColumn(nullable = false)
    private Company company; // Công ty liên quan

}