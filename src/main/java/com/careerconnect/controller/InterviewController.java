package com.careerconnect.controller;

import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.request.InterviewRequest;
import com.careerconnect.dto.request.RescheduleRequest;
import com.careerconnect.dto.response.InterviewResponse;
import com.careerconnect.enums.InterviewStatus;
import com.careerconnect.service.impl.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    /**
     * Schedule a new interview
     */
    @PostMapping("/schedule")
    public ResponseEntity<ApiResp<InterviewResponse>> scheduleInterview(@Valid @RequestBody InterviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(interviewService.scheduleInterview(request));
    }

    /**
     * Reschedule an existing interview
     */
    @PostMapping("/{interviewId}/reschedule")
    public ResponseEntity<ApiResp<InterviewResponse>> rescheduleInterview(
            @PathVariable UUID interviewId,
            @Valid @RequestBody RescheduleRequest request) {
        return ResponseEntity.ok(interviewService.rescheduleInterview(interviewId, request));
    }

    /**
     * Cancel an interview
     */
    @PostMapping("/{interviewId}/cancel")
    public ResponseEntity<ApiResp<Void>> cancelInterview(@PathVariable UUID interviewId) {
        return ResponseEntity.ok(interviewService.cancelInterview(interviewId));
    }

    /**
     * Get interview by ID
     */
    @GetMapping("/{interviewId}")
    public ResponseEntity<ApiResp<InterviewResponse>> getInterviewById(@PathVariable UUID interviewId) {
        return ResponseEntity.ok(interviewService.getInterviewById(interviewId));
    }

    /**
     * Get all interviews for current user
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResp<List<InterviewResponse>>> getUserInterviews() {
        return ResponseEntity.ok(interviewService.getUserInterviews());
    }

    /**
     * Get interviews for a specific application
     */
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<ApiResp<List<InterviewResponse>>> getInterviewsByApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(interviewService.getInterviewsByApplication(applicationId));
    }

    /**
     * Join interview (update status to STARTED)
     */
    @PostMapping("/{interviewId}/join")
    public ResponseEntity<ApiResp<InterviewResponse>> joinInterview(@PathVariable UUID interviewId) {
        return ResponseEntity.ok(interviewService.updateInterviewStatus(interviewId, InterviewStatus.STARTED));
    }

    /**
     * End interview (update status to COMPLETED)
     */
    @PostMapping("/{interviewId}/end")
    public ResponseEntity<ApiResp<InterviewResponse>> endInterview(@PathVariable UUID interviewId) {
        return ResponseEntity.ok(interviewService.updateInterviewStatus(interviewId, InterviewStatus.COMPLETED));
    }
}