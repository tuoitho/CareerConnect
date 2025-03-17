package com.careerconnect.controller;

import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.service.impl.JobAlertSubscriptionService;
import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.dto.request.JobAlertSubscriptionRequest;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoint.PREFIX + "/job-alerts")
@PreAuthorize(SecurityEndpoint.CANDIDATE)
@RequiredArgsConstructor
public class JobAlertSubscriptionController {
    private final JobAlertSubscriptionService jobAlertSubscriptionService;
    private final AuthenticationHelper authenticationHelper;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody JobAlertSubscriptionRequest request) {
        Long candidateId = authenticationHelper.getUserId();
        jobAlertSubscriptionService.subscribe(candidateId, request);
        ApiResponse<?> response = ApiResponse.builder()
                .message("Subscribed to job alerts successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/unsubscribe/{subscriptionId}")
    public ResponseEntity<?> unsubscribe(@PathVariable Long subscriptionId) {
        Long candidateId = authenticationHelper.getUserId();
        jobAlertSubscriptionService.unsubscribe(candidateId, subscriptionId);
        ApiResponse<?> response = ApiResponse.builder()
                .message("Unsubscribed from job alerts successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getJobAlertSubscriptions() {
        Long candidateId = authenticationHelper.getUserId();
        ApiResponse<?> response = ApiResponse.builder()
                .result(jobAlertSubscriptionService.getJobAlertSubscriptions(candidateId))
                .build();
        return ResponseEntity.ok(response);
    }
}