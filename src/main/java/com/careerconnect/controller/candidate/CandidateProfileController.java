package com.careerconnect.controller.candidate;

import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.dto.request.CandidateProfileRequest;
import com.careerconnect.dto.response.CandidateProfileResponse;
import com.careerconnect.service.impl.CandidateProfileService;
import com.careerconnect.util.AuthenticationHelper;
import com.careerconnect.util.Logger;
import com.cloudinary.Api;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @PostMapping(value = "/upload-cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCV(@RequestPart String cvName,
                                      @RequestParam("file") MultipartFile file) {
        Long candidateId = authenticationHelper.getUserId();
        ApiResponse<CandidateProfileResponse.CVResponse> response = ApiResponse.<CandidateProfileResponse.CVResponse>builder()
                .message("CV uploaded successfully")
                .result(candidateService.uploadCV(candidateId, cvName, file))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-cv/{cvId}")
    public ResponseEntity<?> deleteCV(@PathVariable Long cvId) {
        Long candidateId = authenticationHelper.getUserId();
        ApiResponse<CandidateProfileResponse.CVResponse> response = ApiResponse.<CandidateProfileResponse.CVResponse>builder()
                .message("CV deleted successfully")
                .result(candidateService.deleteCV(candidateId, cvId))
                .build();
        return ResponseEntity.ok(response);
    }
}
