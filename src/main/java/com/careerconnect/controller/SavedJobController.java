package com.careerconnect.controller;


import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.response.SavedJobResponseDTO;
import com.careerconnect.service.impl.SavedJobService;
import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-jobs")
@RequiredArgsConstructor
@PreAuthorize(SecurityEndpoint.CANDIDATE)
public class SavedJobController {
    private final SavedJobService savedJobService;
    private final AuthenticationHelper authenticationHelper;

    @PostMapping("/{jobId}")
    public ResponseEntity<?> saveJob(
        @PathVariable Long jobId
    ) {
        Long candidateId = authenticationHelper.getUserId();
        SavedJobResponseDTO response = savedJobService.saveJob(candidateId, jobId);
        ApiResponse<?> apiResponse = ApiResponse.builder()
            .message("Job saved successfully")
            .result(response)
            .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<?> getSavedJobs(
    ) {
        Long candidateId = authenticationHelper.getUserId();
        List<SavedJobResponseDTO> savedJobs = savedJobService.getSavedJobs(candidateId);
        ApiResponse<?> apiResponse = ApiResponse.builder()
            .result(savedJobs)
            .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> unsaveJob(
        @PathVariable Long jobId
    ) {
        Long candidateId = authenticationHelper.getUserId();
        savedJobService.unsaveJob(candidateId, jobId);
        ApiResponse<?> apiResponse = ApiResponse.builder()
            .message("Job unsaved successfully")
            .build();
        return ResponseEntity.ok(apiResponse);
    }
}