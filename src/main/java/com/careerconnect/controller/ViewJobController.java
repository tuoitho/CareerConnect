package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.service.impl.JobService;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoint.PREFIX+"/company/jobs")
public class ViewJobController {
    private final JobService jobService;
    private final AuthenticationHelper authenticationHelper;
    @GetMapping("")
    public ResponseEntity<?> getCompanyJobs(@RequestParam Long companyId, @RequestParam int page, @RequestParam int size) {
        var jobs = jobService.getCompanyJobs(companyId, page, size);
        ApiResponse<?> response = ApiResponse.builder()
                .message("Jobs retrieved successfully")
                .result(jobs)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetailJobById(@PathVariable Long id) {
        var job = jobService.getJobById(id);
        ApiResponse<?> response = ApiResponse.builder()
                .message("Job retrieved successfully")
                .result(job)
                .build();
        return ResponseEntity.ok(response);
    }
}
