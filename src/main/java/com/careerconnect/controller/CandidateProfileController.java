package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.dto.request.CandidateProfileRequest;
import com.careerconnect.dto.response.CandidateProfileResponse;
import com.careerconnect.service.impl.CandidateProfileService;
import com.careerconnect.util.AuthenticationHelper;
import com.careerconnect.util.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoint.PREFIX+"/candidate/profile")
public class CandidateProfileController {

    private final CandidateProfileService candidateService;
    private final AuthenticationHelper authenticationHelper;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> getProfile() {
        Long candidateId = authenticationHelper.getUserId();
        ApiResponse<CandidateProfileResponse> response = ApiResponse.<CandidateProfileResponse>builder()
                .message("Profile retrieved successfully")
                .result(candidateService.getProfile(candidateId))
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(@RequestPart String profile,
                                           @RequestPart(required = false) MultipartFile avatar
                                           ) throws JsonProcessingException {
        Long candidateId = authenticationHelper.getUserId();
        ObjectMapper objectMapper = new ObjectMapper();
        Logger.log("Profile: " + profile);
        CandidateProfileRequest profileRequest = objectMapper.readValue(profile, CandidateProfileRequest.class);

        ApiResponse<CandidateProfileResponse> response = ApiResponse.<CandidateProfileResponse>builder()
                .message("Profile updated successfully")
                .result(candidateService.updateProfile(candidateId, profileRequest, avatar))
                .build();
        return ResponseEntity.ok(response);
    }

    //get all my CV
    @GetMapping("/cv")
    public ResponseEntity<?> getCVs() {
        Long candidateId = authenticationHelper.getUserId();
        ApiResponse<?> response = ApiResponse.builder()
                .message("CVs retrieved successfully")
                .result(candidateService.getCVs(candidateId))
                .build();
        return ResponseEntity.ok(response);
    }
    @PostMapping(value = "/cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCV(@RequestPart String cvName,
                                      @RequestParam("file") MultipartFile file) {
        Long candidateId = authenticationHelper.getUserId();
        ApiResponse<CandidateProfileResponse.CVResponse> response = ApiResponse.<CandidateProfileResponse.CVResponse>builder()
                .message("CV uploaded successfully")
                .result(candidateService.uploadCV(candidateId, cvName, file))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cv/{cvId}")
    public ResponseEntity<?> deleteCV(@PathVariable Long cvId) {
        Long candidateId = authenticationHelper.getUserId();
        ApiResponse<CandidateProfileResponse.CVResponse> response = ApiResponse.<CandidateProfileResponse.CVResponse>builder()
                .message("CV deleted successfully")
                .result(candidateService.deleteCV(candidateId, cvId))
                .build();
        return ResponseEntity.ok(response);
    }
}
