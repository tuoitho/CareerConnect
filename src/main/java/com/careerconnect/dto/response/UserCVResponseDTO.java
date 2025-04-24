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
public class UserCVResponseDTO {
    private Long id;
    private Long userId;
    private String name;
    private String templateId;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}