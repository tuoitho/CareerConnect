package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.request.RecruiterProfileRequest;
import com.careerconnect.service.impl.RecruiterProfileService;
import com.careerconnect.util.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(ApiEndpoint.PREFIX+"/recruiter/profile")
@RequiredArgsConstructor
@PreAuthorize(SecurityEndpoint.RECRUITER)
@Tag(name = "Recruiter Profile", description = "API quản lý hồ sơ nhà tuyển dụng")
@SecurityRequirement(name = "bearerAuth")
public class RecruiterProfileController {
    private final RecruiterProfileService recruiterProfileService;
    private final AuthenticationHelper authenticationHelper;

    @Operation(summary = "Lấy thông tin hồ sơ", description = "API lấy thông tin hồ sơ của nhà tuyển dụng đang đăng nhập")
    @GetMapping()
    public ResponseEntity<?> getProfile() {
        ApiResp<?> response = ApiResp.builder()
                .message("Company retrieved successfully")
                .result(recruiterProfileService.getProfile(authenticationHelper.getUserId()))
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Cập nhật hồ sơ", description = "API cập nhật thông tin hồ sơ của nhà tuyển dụng")
    @PutMapping()
    public ResponseEntity<?> updateProfile(@Valid @RequestBody  RecruiterProfileRequest req) {
        ApiResp<?> response = ApiResp.builder()
                .message("Company updated successfully")
                .result(recruiterProfileService.updateProfile(authenticationHelper.getUserId(), req))
                .build();
        return ResponseEntity.ok(response);
    }
}
