package com.careerconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllInvitationResponse {
    private Long id;
    private String email; // Email của người được mời
    private LocalDateTime expiryDate; // Thời hạn lời mời
    private boolean accepted; // Trạng thái chấp nhận lời mời
    private String inviterName; // Người gửi lời mời
}
