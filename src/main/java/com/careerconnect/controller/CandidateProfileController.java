package com.careerconnect.controller;

import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.response.SimpleCandidateResponse;
import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.request.CandidateProfileRequest;
import com.careerconnect.dto.response.CandidateDetailResponse;
import com.careerconnect.dto.response.CandidateProfileResponse;
import com.careerconnect.service.impl.CandidateProfileService;
import com.careerconnect.util.AuthenticationHelper;
import com.careerconnect.util.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoint.PREFIX+"/candidate/profile")
@Tag(name = "Candidate Profile", description = "API quản lý hồ sơ ứng viên")
@SecurityRequirement(name = "bearerAuth")
public class CandidateProfileController {

    private final CandidateProfileService candidateService;
    private final AuthenticationHelper authenticationHelper;

    @Operation(summary = "Lấy thông tin hồ sơ cá nhân", description = "API lấy thông tin hồ sơ của ứng viên đăng nhập")
    @PreAuthorize(SecurityEndpoint.CANDIDATE)
    @GetMapping("/me")
    public ResponseEntity<ApiResp<CandidateProfileResponse>> getProfile() {
        Long candidateId = authenticationHelper.getUserId();
        ApiResp<CandidateProfileResponse> response = ApiResp.<CandidateProfileResponse>builder()
                .message("Profile retrieved successfully")
                .result(candidateService.getProfile(candidateId))
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cập nhật hồ sơ cá nhân", description = "API cập nhật thông tin hồ sơ và ảnh đại diện của ứng viên")
    @PreAuthorize(SecurityEndpoint.CANDIDATE)
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @Parameter(description = "Thông tin hồ sơ dạng JSON string", required = true)
            @RequestPart String profile,
            @Parameter(description = "Ảnh đại diện (không bắt buộc)")
            @RequestPart(required = false) MultipartFile avatar
            ) throws JsonProcessingException {
        Long candidateId = authenticationHelper.getUserId();
        ObjectMapper objectMapper = new ObjectMapper();
        Logger.log("Profile: " + profile);
        CandidateProfileRequest profileRequest = objectMapper.readValue(profile, CandidateProfileRequest.class);

        ApiResp<CandidateProfileResponse> response = ApiResp.<CandidateProfileResponse>builder()
                .message("Profile updated successfully")
                .result(candidateService.updateProfile(candidateId, profileRequest, avatar))
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lấy danh sách CV", description = "API lấy danh sách CV của ứng viên đăng nhập")
    @PreAuthorize(SecurityEndpoint.CANDIDATE)
    @GetMapping("/cv")
    public ResponseEntity<?> getCVs() {
        Long candidateId = authenticationHelper.getUserId();
        ApiResp<?> response = ApiResp.builder()
                .message("CVs retrieved successfully")
                .result(candidateService.getCVs(candidateId))
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Tải lên CV mới", description = "API tải lên CV mới cho ứng viên")
    @PreAuthorize(SecurityEndpoint.CANDIDATE)
    @PostMapping(value = "/cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCV(
            @Parameter(description = "Tên của CV", required = true)
            @RequestPart String cvName,
            @Parameter(description = "File CV (PDF, Word,...)", required = true)
            @RequestParam("file") MultipartFile file) {
        Long candidateId = authenticationHelper.getUserId();
        ApiResp<CandidateProfileResponse.CVResponse> response = ApiResp.<CandidateProfileResponse.CVResponse>builder()
                .message("CV uploaded successfully")
                .result(candidateService.uploadCV(candidateId, cvName, file))
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Xóa CV", description = "API xóa CV của ứng viên theo ID")
    @PreAuthorize(SecurityEndpoint.CANDIDATE)
    @DeleteMapping("/cv/{cvId}")
    public ResponseEntity<?> deleteCV(
            @Parameter(description = "ID của CV cần xóa", required = true)
            @PathVariable Long cvId) {
        Long candidateId = authenticationHelper.getUserId();
        ApiResp<CandidateProfileResponse.CVResponse> response = ApiResp.<CandidateProfileResponse.CVResponse>builder()
                .message("CV deleted successfully")
                .result(candidateService.deleteCV(candidateId, cvId))
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Xem thông tin chi tiết ứng viên", description = "API cho nhà tuyển dụng xem thông tin chi tiết của ứng viên")
    @PreAuthorize(SecurityEndpoint.RECRUITER)
    @GetMapping("/{candidateId}")
    public ResponseEntity<?> getCandidateDetail(
            @Parameter(description = "ID của ứng viên", required = true)
            @PathVariable Long candidateId) {
        CandidateDetailResponse response = candidateService.getCandidateDetail(candidateId);

        ApiResp<CandidateDetailResponse> apiResponse = ApiResp.<CandidateDetailResponse>builder()
                .result(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Lấy thông tin ứng viên cho chat", description = "API lấy thông tin cơ bản của ứng viên cho chức năng chat")
    @PreAuthorize(SecurityEndpoint.RECRUITER)
    @GetMapping("/chat/{candidateId}")
    public ResponseEntity<?> getCandidateForChat(
            @Parameter(description = "ID của ứng viên", required = true)
            @PathVariable Long candidateId) {
        SimpleCandidateResponse response = candidateService.getCandidateForChat(candidateId);
        ApiResp<SimpleCandidateResponse> apiResponse = ApiResp.<SimpleCandidateResponse>builder()
                .result(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
