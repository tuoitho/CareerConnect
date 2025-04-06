package com.careerconnect.controller;


import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.response.SavedJobResponseDTO;
import com.careerconnect.service.impl.SavedJobService;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.util.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-jobs")
@RequiredArgsConstructor
@PreAuthorize(SecurityEndpoint.CANDIDATE)
@Tag(name = "Saved Jobs", description = "API quản lý công việc đã lưu của ứng viên")
@SecurityRequirement(name = "bearerAuth")
public class SavedJobController {
    private final SavedJobService savedJobService;
    private final AuthenticationHelper authenticationHelper;

    @Operation(summary = "Lưu công việc", description = "API lưu một công việc vào danh sách yêu thích của ứng viên")
    @PostMapping("/{jobId}")
    public ResponseEntity<?> saveJob(
        @PathVariable Long jobId
    ) {
        Long candidateId = authenticationHelper.getUserId();
        SavedJobResponseDTO response = savedJobService.saveJob(candidateId, jobId);
        ApiResp<?> apiResponse = ApiResp.builder()
            .message("Job saved successfully")
            .result(response)
            .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Lấy danh sách công việc đã lưu", description = "API lấy tất cả công việc đã được ứng viên lưu")
    @GetMapping
    public ResponseEntity<?> getSavedJobs(
    ) {
        Long candidateId = authenticationHelper.getUserId();
        List<SavedJobResponseDTO> savedJobs = savedJobService.getSavedJobs(candidateId);
        ApiResp<?> apiResponse = ApiResp.builder()
            .result(savedJobs)
            .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Bỏ lưu công việc", description = "API xóa một công việc khỏi danh sách đã lưu của ứng viên")
    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> unsaveJob(
        @PathVariable Long jobId
    ) {
        Long candidateId = authenticationHelper.getUserId();
        savedJobService.unsaveJob(candidateId, jobId);
        ApiResp<?> apiResponse = ApiResp.builder()
            .message("Job unsaved successfully")
            .build();
        return ResponseEntity.ok(apiResponse);
    }
}