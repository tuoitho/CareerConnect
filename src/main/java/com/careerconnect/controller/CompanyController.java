package com.careerconnect.controller;

import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.dto.request.AddMemberRequest;
import com.careerconnect.dto.request.RegisterCompanyRequest;
import com.careerconnect.service.ImageService;
import com.careerconnect.service.impl.CompanyService;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //add member

    @PostMapping("/company/addmember")
    public ResponseEntity<?> addMember(@RequestBody AddMemberRequest addMemberRequest) {
        companyService.addMember(authenticationHelper.getUserId(),addMemberRequest);
        ApiResponse<?> response = ApiResponse.builder()
                .message("Mời thành viên thành công")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/company/accept")
    public ResponseEntity<?> joinCompany(@RequestParam String token) {
        Long userId = authenticationHelper.getUserId();
        ApiResponse<?> response = ApiResponse.builder()
                .message("Tham gia công ty thành công")
                .result(companyService.accept(userId,token))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/invitation/{token}")
    public ResponseEntity<?> getInvitation(@PathVariable String token) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Lấy thông tin thành công")
                .result(companyService.getInvitation(token))
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/invitation")
    public ResponseEntity<?> getInvitations(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "2") int size) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Lấy thông tin thành công")
                .result(companyService.getInvitations(authenticationHelper.getUserId(),page,size))
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/company/member")
    public ResponseEntity<?> getMembers( @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "2") int size) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Lấy thông tin thành công")
                .result(companyService.getMembers(authenticationHelper.getUserId(),page,size))
                .build();
        return ResponseEntity.ok(response);
    }
}
