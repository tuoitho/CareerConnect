package com.careerconnect.controller.candidate;

import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.dto.request.CandidateProfileRequest;
import com.careerconnect.dto.response.CandidateProfileResponse;
import com.careerconnect.service.impl.CandidateProfileService;
import com.careerconnect.util.AuthenticationHelper;
import com.cloudinary.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidate/profile")
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileService candidateService;
    private final AuthenticationHelper authenticationHelper;

    @GetMapping("")
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> getProfile() {
        Long candidateId = authenticationHelper.getUserId();
        ApiResponse<CandidateProfileResponse> response = ApiResponse.<CandidateProfileResponse>builder()
                .message("Profile retrieved successfully")
                .result(candidateService.getProfile(candidateId))
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody CandidateProfileRequest request) {
        Long candidateId = authenticationHelper.getUserId();
        ApiResponse<CandidateProfileResponse> response = ApiResponse.<CandidateProfileResponse>builder()
                .message("Profile updated successfully")
                .result(candidateService.updateProfile(candidateId, request))
                .build();
        return ResponseEntity.ok(response);
    }
}
