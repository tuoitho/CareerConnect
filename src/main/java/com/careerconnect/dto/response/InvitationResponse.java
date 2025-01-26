package com.careerconnect.dto.response;

import com.careerconnect.entity.Company;
import com.careerconnect.entity.Recruiter;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationResponse {
    private Long id;
    private String token; // Token để xác thực lời mời
    private String email; // Email của người được mời
    private LocalDateTime expiryDate; // Thời hạn lời mời
    private boolean accepted; // Trạng thái chấp nhận lời mời
    private String inviterName; // Người gửi lời mời
    private String companyName; // Công ty liên quan
}
