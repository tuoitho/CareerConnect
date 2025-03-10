package com.careerconnect.dto.response;

import com.careerconnect.entity.Candidate;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobAlertSubscriptionResponse {

    private Long id;
    private String keyword;
    private boolean active;

}
