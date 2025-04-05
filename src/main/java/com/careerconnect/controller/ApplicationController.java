package com.careerconnect.controller;

import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.response.ApplicationDetailResponse;
import com.careerconnect.service.impl.ApplicationService;
import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiEndpoint.PREFIX+"/application")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;
    private final AuthenticationHelper authenticationHelper;

    @PreAuthorize(SecurityEndpoint.RECRUITER)
    @GetMapping("/{applicationId}")
    public ResponseEntity<?> getApplicationDetail(@PathVariable Long applicationId) {
        Long recruiterId = authenticationHelper.getUserId();
        ApplicationDetailResponse response = applicationService.getApplicationDetail(recruiterId, applicationId);

        ApiResp<ApplicationDetailResponse> apiResponse = ApiResp.<ApplicationDetailResponse>builder()
                .message("Application details retrieved successfully")
                .result(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
