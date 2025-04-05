package com.careerconnect.controller;

import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.service.impl.JobAlertSubscriptionService;
import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.request.JobAlertSubscriptionRequest;
import com.careerconnect.util.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoint.PREFIX + "/job-alerts")
@PreAuthorize(SecurityEndpoint.CANDIDATE)
@RequiredArgsConstructor
@Tag(name = "Job Alerts", description = "API quản lý đăng ký thông báo việc làm")
@SecurityRequirement(name = "bearerAuth")
public class JobAlertSubscriptionController {
    private final JobAlertSubscriptionService jobAlertSubscriptionService;
    private final AuthenticationHelper authenticationHelper;

    @Operation(summary = "Đăng ký thông báo việc làm", description = "API đăng ký nhận thông báo về việc làm phù hợp với tiêu chí")
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@Valid @RequestBody JobAlertSubscriptionRequest request) {
        Long candidateId = authenticationHelper.getUserId();
        jobAlertSubscriptionService.subscribe(candidateId, request);
        ApiResp<?> response = ApiResp.builder()
                .message("Subscribed to job alerts successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Hủy đăng ký thông báo", description = "API hủy đăng ký nhận thông báo việc làm")
    @DeleteMapping("/unsubscribe/{subscriptionId}")
    public ResponseEntity<?> unsubscribe(@PathVariable Long subscriptionId) {
        Long candidateId = authenticationHelper.getUserId();
        jobAlertSubscriptionService.unsubscribe(candidateId, subscriptionId);
        ApiResp<?> response = ApiResp.builder()
                .message("Unsubscribed from job alerts successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Danh sách đăng ký thông báo", description = "API lấy danh sách các thông báo việc làm đã đăng ký")
    @GetMapping
    public ResponseEntity<?> getJobAlertSubscriptions() {
        Long candidateId = authenticationHelper.getUserId();
        ApiResp<?> response = ApiResp.builder()
                .result(jobAlertSubscriptionService.getJobAlertSubscriptions(candidateId))
                .build();
        return ResponseEntity.ok(response);
    }
}