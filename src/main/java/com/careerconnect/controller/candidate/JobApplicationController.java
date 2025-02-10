package com.careerconnect.controller.candidate;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.dto.request.ApplyJobRequest;
import com.careerconnect.service.impl.JobService;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoint.JOBAPPLICATION)
@RequiredArgsConstructor
public class JobApplicationController  {
    private final JobService jobService;
    private final AuthenticationHelper authenticationHelper;

    @PostMapping("/apply")
    public ResponseEntity<?> applyJob(@RequestBody ApplyJobRequest request) {
        // Logic xử lý apply job
        Long candidateId = authenticationHelper.getUserId();
        jobService.applyJob(candidateId, request);
        ApiResponse<?> response = ApiResponse.builder()
                .message("Applied successfully")
                .build();
        return ResponseEntity.ok(response);
    }
    //get list job đã apply
    @GetMapping("/applied")
    public ResponseEntity<?> getAppliedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Logic lấy danh sách job đã apply
        Long candidateId = authenticationHelper.getUserId();
        ApiResponse<?> response = ApiResponse.builder()
                .message("Get applied jobs successfully")
                .result(jobService.getAppliedJobs(candidateId,page, size))
                .build();
        return ResponseEntity.ok(response);
    }
//    @GetMapping("/candidates/applications/{applicationId}")
//    public ResponseEntity<ApplicationDto> getApplicationDetails(@PathVariable Long applicationId) {
//        // Logic lấy chi tiết đơn ứng tuyển
//        return ResponseEntity.ok(applicationService.getApplicationDetails(applicationId));
//    }
}
