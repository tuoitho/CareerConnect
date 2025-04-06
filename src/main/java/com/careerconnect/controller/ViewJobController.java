package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.response.JobDetailResponse;
import com.careerconnect.service.impl.JobService;
import com.careerconnect.service.impl.SearchJobService;
import com.careerconnect.util.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoint.PREFIX+"/company/jobs")
@Tag(name = "Job Management", description = "API quản lý và xem thông tin công việc")
public class ViewJobController {
    private final JobService jobService;
    private final AuthenticationHelper authenticationHelper;
    private final SearchJobService searchJobService;

    @Operation(summary = "Lấy danh sách công việc của một công ty", description = "API lấy danh sách công việc theo ID công ty")
    @GetMapping("")
    public ResponseEntity<?> getCompanyJobs(
            @Parameter(description = "ID của công ty", required = true) 
            @RequestParam Long companyId, 
            @Parameter(description = "Số trang, bắt đầu từ 0") 
            @RequestParam int page, 
            @Parameter(description = "Số lượng kết quả mỗi trang") 
            @RequestParam int size) {
        var jobs = jobService.getCompanyJobs(companyId, page, size);
        ApiResp<?> response = ApiResp.builder()
                .message("Jobs retrieved successfully")
                .result(jobs)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Xem chi tiết công việc", description = "API lấy thông tin chi tiết của một công việc theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getDetailJobById(
            @Parameter(description = "ID của công việc", required = true) 
            @PathVariable Long id) {
        Long candidateId = authenticationHelper.getUserId();
        JobDetailResponse job = jobService.getJobDetailById(candidateId,id);
        ApiResp<?> response = ApiResp.builder()
                .message("Job retrieved successfully")
                .result(job)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Tìm kiếm công việc", description = "API tìm kiếm công việc theo từ khóa")
    @GetMapping("/search")
    public ResponseEntity<?> searchJobs(
            @Parameter(description = "Từ khóa tìm kiếm") 
            @RequestParam(defaultValue = "") String query, 
            @Parameter(description = "Số trang, bắt đầu từ 0") 
            @RequestParam(defaultValue = "0") int page, 
            @Parameter(description = "Số lượng kết quả mỗi trang") 
            @RequestParam(defaultValue = "3") int size) {
        var jobs = jobService.searchJobs(query, page, size);
        ApiResp<?> response = ApiResp.builder()
                .message("Jobs retrieved successfully")
                .result(jobs)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Xem ứng viên ứng tuyển", description = "API cho phép ứng viên xem thông tin ứng tuyển (yêu cầu quyền ứng viên)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize(SecurityEndpoint.CANDIDATE)
    @PostMapping("/{jobId}/view-applicants")
    public ResponseEntity<?> viewApplicants(
            @Parameter(description = "ID của công việc", required = true)
            @PathVariable Long jobId) {
        Long userId = authenticationHelper.getUserId();
        Long responseData = jobService.viewApplicants(userId, jobId);

        ApiResp<?> response = ApiResp.builder()
                .result(responseData)
                .build();
        return ResponseEntity.ok(response);
    }
}
