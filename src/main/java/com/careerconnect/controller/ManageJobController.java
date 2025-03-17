package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.dto.request.CreateJobRequest;
import com.careerconnect.service.impl.JobService;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoint.PREFIX+"/recruiter/jobs")
@RequiredArgsConstructor
@PreAuthorize(SecurityEndpoint.RECRUITER)
public class ManageJobController {
    private final JobService jobService;
    private final AuthenticationHelper authenticationHelper;
    @GetMapping
    public ResponseEntity<?> getJobs(@RequestParam int page, @RequestParam int size) {
        var jobs = jobService.getJobs(authenticationHelper.getUserId(),page, size);

        ApiResponse<?> response = ApiResponse.builder()
                .message("Jobs retrieved successfully")
                .result(jobs)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<?> getJobById(@PathVariable Long jobId) {
        var job = jobService.getPostedJobDetail(jobId);
        ApiResponse<?> response = ApiResponse.builder()
                .message("Job Details retrieved successfully")
                .result(job)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody CreateJobRequest job) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Job created successfully")
                .result(jobService.createJob(authenticationHelper.getUserId(),job))
                .build();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody CreateJobRequest job) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Job updated successfully")
                .result(jobService.updateJob(authenticationHelper.getUserId(), id, job))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Job deleted successfully")
                .build();
        jobService.deleteJob(authenticationHelper.getUserId(), id);
        return ResponseEntity.ok(response);
    }

}
