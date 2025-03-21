package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.dto.request.AddMemberRequest;
import com.careerconnect.dto.request.RegisterCompanyRequest;
import com.careerconnect.service.ImageService;
import com.careerconnect.service.impl.CompanyService;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoint.PREFIX+"/company")
@PreAuthorize(SecurityEndpoint.RECRUITER)
public class ManageCompanyController {
    private final CompanyService companyService;
    private final AuthenticationHelper authenticationHelper;
    private final ImageService imageService;
    @GetMapping("/mycompany")
    public ResponseEntity<?> getCompany() {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Company retrieved successfully")
                .result(companyService.getCurrentCompany(authenticationHelper.getUserId()))
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/mycompany/members")
    public ResponseEntity<?> getMembers(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "2") int size) {
        ApiResponse<?> response = ApiResponse.builder()
                .result(companyService.getMembers(authenticationHelper.getUserId(),page,size))
                .build();
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/mycompany/members/{id}")
    public ResponseEntity<?> getMembers(@PathVariable long id) {
        companyService.removeMember(authenticationHelper.getUserId(),id);
        ApiResponse<?> response = ApiResponse.builder()
                .message("Member removed successfully")
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    @PreAuthorize(SecurityEndpoint.BOTH)
    public ResponseEntity<?> getCompanyById(@PathVariable Long id) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Company retrieved successfully")
                .result(companyService.getCompanyById(id))
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
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

    @PutMapping("/mycompany")
    public ResponseEntity<?> updateCompany(@ModelAttribute RegisterCompanyRequest registerCompanyRequest) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Company updated successfully")
                .result(companyService.updateCompany(authenticationHelper.getUserId(), registerCompanyRequest))
                .build();
        return ResponseEntity.ok(response);
    }

}
