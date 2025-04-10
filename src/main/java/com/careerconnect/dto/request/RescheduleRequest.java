package com.careerconnect.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RescheduleRequest {
    @NotNull(message = "Scheduled time is required")
    @Future(message = "Scheduled time must be in the future")
    private OffsetDateTime scheduledTime;

    private String message;
}
