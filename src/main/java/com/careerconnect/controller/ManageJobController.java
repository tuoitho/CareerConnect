package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.request.CreateJobRequest;
import com.careerconnect.service.impl.JobService;
import com.careerconnect.util.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoint.PREFIX+"/recruiter/jobs")
@RequiredArgsConstructor
@PreAuthorize(SecurityEndpoint.RECRUITER)
@Tag(name = "Job Management", description = "API quản lý công việc dành cho nhà tuyển dụng")
@SecurityRequirement(name = "bearerAuth")
public class ManageJobController {
    private final JobService jobService;
    private final AuthenticationHelper authenticationHelper;
    
    @Operation(summary = "Lấy danh sách công việc", description = "API lấy danh sách các công việc mà nhà tuyển dụng đã đăng")
    @GetMapping
    public ResponseEntity<?> getJobs(@RequestParam int page, @RequestParam int size) {
        var jobs = jobService.getJobs(authenticationHelper.getUserId(),page, size);

        ApiResp<?> response = ApiResp.builder()
                .message("Jobs retrieved successfully")
                .result(jobs)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Xem chi tiết công việc", description = "API xem thông tin chi tiết của một công việc đã đăng theo ID")
    @GetMapping("/{jobId}")
    public ResponseEntity<?> getJobById(@PathVariable Long jobId) {
        var job = jobService.getPostedJobDetail(jobId);
        ApiResp<?> response = ApiResp.builder()
                .message("Job Details retrieved successfully")
                .result(job)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Tạo công việc mới", description = "API đăng tin tuyển dụng công việc mới")
    @PostMapping
    public ResponseEntity<?> createJob(@Valid @RequestBody CreateJobRequest job) {
        ApiResp<?> response = ApiResp.builder()
                .message("Job created successfully")
                .result(jobService.createJob(authenticationHelper.getUserId(),job))
                .build();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @Operation(summary = "Cập nhật công việc", description = "API cập nhật thông tin công việc đã đăng")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @Valid @RequestBody CreateJobRequest job) {
        ApiResp<?> response = ApiResp.builder()
                .message("Job updated successfully")
                .result(jobService.updateJob(authenticationHelper.getUserId(), id, job))
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Xóa công việc", description = "API xóa công việc đã đăng")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        ApiResp<?> response = ApiResp.builder()
                .message("Job deleted successfully")
                .build();
        jobService.deleteJob(authenticationHelper.getUserId(), id);
        return ResponseEntity.ok(response);
    }
}
