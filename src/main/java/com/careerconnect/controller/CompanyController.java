package com.careerconnect.controller;

import com.careerconnect.dto.ApiResponse;
import com.careerconnect.dto.request.RegisterCompanyRequest;
import com.careerconnect.service.ImageService;
import com.careerconnect.service.impl.CompanyService;
import com.careerconnect.util.AuthenticationHelper;
import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;
    private final AuthenticationHelper authenticationHelper;
    private final ImageService imageService;
    @GetMapping("/company")
    public ResponseEntity<?> getCompany() {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Company retrieved successfully")
                .result(companyService.getCurrentCompany(authenticationHelper.getUserId()))
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/company")
    public ResponseEntity<?> registerCompany(@ModelAttribute RegisterCompanyRequest registerCompanyRequest) {
//        MultipartFile logoFile = registerCompanyRequest.getLogo();
////        String logoUrl = null;
////        if (logoFile != null && !logoFile.isEmpty()) {
////            // Lưu file vào thư mục hoặc xử lý tùy ý
////            logoUrl = imageService.uploadCloudinary(logoFile);
////        }
        ApiResponse<?> response = ApiResponse.builder()
                .message("Company registered successfully")
                .result(companyService.registerCompany(authenticationHelper.getUserId(), registerCompanyRequest))
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/company")
    public ResponseEntity<?> updateCompany(@ModelAttribute RegisterCompanyRequest registerCompanyRequest) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Company updated successfully")
                .result(companyService.updateCompany(authenticationHelper.getUserId(), registerCompanyRequest))
                .build();
        return ResponseEntity.ok(response);
    }
}
