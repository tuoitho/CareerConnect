package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.request.ApplyJobRequest;
import com.careerconnect.service.impl.JobService;
import com.careerconnect.util.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(ApiEndpoint.PREFIX+"/job")
@RequiredArgsConstructor
@PreAuthorize(SecurityEndpoint.CANDIDATE)
@Tag(name = "Job Application", description = "API liên quan đến ứng tuyển công việc")
@SecurityRequirement(name = "bearerAuth")
public class ApplyJobController {
    private final JobService jobService;
    private final AuthenticationHelper authenticationHelper;

    @Operation(summary = "Ứng tuyển công việc", description = "API cho phép ứng viên nộp đơn ứng tuyển công việc")
    @PostMapping("/apply")
    public ResponseEntity<?> applyJob(
            @Parameter(description = "Thông tin ứng tuyển công việc", required = true)
            @Valid @RequestBody ApplyJobRequest request) {
        // Logic xử lý apply job
        Long candidateId = authenticationHelper.getUserId();
        jobService.applyJob(candidateId, request);
        ApiResp<?> response = ApiResp.builder()
                .message("Applied successfully")
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Lấy danh sách công việc đã ứng tuyển", description = "API lấy danh sách các công việc mà ứng viên đã nộp đơn ứng tuyển")
    @GetMapping("/applied")
    public ResponseEntity<?> getAppliedJobs(
            @Parameter(description = "Số trang, bắt đầu từ 0") 
            @RequestParam(defaultValue = "0", required = false) int page,
            @Parameter(description = "Số lượng kết quả mỗi trang") 
            @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("Get applied jobs");

        // Logic lấy danh sách job đã apply
        Long candidateId = authenticationHelper.getUserId();
        ApiResp<?> response = ApiResp.builder()
                .message("Get applied jobs successfully")
                .result(jobService.getAppliedJobs(candidateId,page, size))
                .build();
        return ResponseEntity.ok(response);
    }
}
