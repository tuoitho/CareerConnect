package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.request.RegisterCompanyRequest;
import com.careerconnect.service.ImageService;
import com.careerconnect.service.impl.CompanyService;
import com.careerconnect.util.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoint.PREFIX+"/company")
@PreAuthorize(SecurityEndpoint.RECRUITER)
@Tag(name = "Company Management", description = "API quản lý thông tin công ty của nhà tuyển dụng")
@SecurityRequirement(name = "bearerAuth")
public class ManageCompanyController {
    private final CompanyService companyService;
    private final AuthenticationHelper authenticationHelper;
    private final ImageService imageService;
    
    @Operation(summary = "Lấy thông tin công ty", description = "API lấy thông tin công ty của nhà tuyển dụng đang đăng nhập")
    @GetMapping("/mycompany")
    public ResponseEntity<?> getCompany() {
        ApiResp<?> response = ApiResp.builder()
                .message("Company retrieved successfully")
                .result(companyService.getCurrentCompany(authenticationHelper.getUserId()))
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Lấy danh sách thành viên công ty", description = "API lấy danh sách thành viên của công ty")
    @GetMapping("/mycompany/members")
    public ResponseEntity<?> getMembers(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "2") int size) {
        ApiResp<?> response = ApiResp.builder()
                .result(companyService.getMembers(authenticationHelper.getUserId(),page,size))
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Xóa thành viên công ty", description = "API xóa một thành viên khỏi công ty")
    @DeleteMapping("/mycompany/members/{id}")
    public ResponseEntity<?> getMembers(@PathVariable long id) {
        companyService.removeMember(authenticationHelper.getUserId(),id);
        ApiResp<?> response = ApiResp.builder()
                .message("Member removed successfully")
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Xem thông tin công ty theo ID", description = "API xem thông tin chi tiết của một công ty theo ID")
    @GetMapping("/{id}")
    @PreAuthorize(SecurityEndpoint.BOTH)
    public ResponseEntity<?> getCompanyById(@PathVariable Long id) {
        ApiResp<?> response = ApiResp.builder()
                .message("Company retrieved successfully")
                .result(companyService.getCompanyById(id))
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Đăng ký công ty mới", description = "API đăng ký thông tin công ty mới cho nhà tuyển dụng")
    @PostMapping("/register")
    public ResponseEntity<?> registerCompany(@ModelAttribute RegisterCompanyRequest registerCompanyRequest) {
//        MultipartFile logoFile = registerCompanyRequest.getLogo();
////        String logoUrl = null;
////        if (logoFile != null && !logoFile.isEmpty()) {
////            // Lưu file vào thư mục hoặc xử lý tùy ý
////            logoUrl = imageService.uploadCloudinary(logoFile);
////        }
        ApiResp<?> response = ApiResp.builder()
                .message("Company registered successfully")
                .result(companyService.registerCompany(authenticationHelper.getUserId(), registerCompanyRequest))
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cập nhật thông tin công ty", description = "API cập nhật thông tin công ty hiện tại")
    @PutMapping("/mycompany")
    public ResponseEntity<?> updateCompany(@ModelAttribute RegisterCompanyRequest registerCompanyRequest) {
        ApiResp<?> response = ApiResp.builder()
                .message("Company updated successfully")
                .result(companyService.updateCompany(authenticationHelper.getUserId(), registerCompanyRequest))
                .build();
        return ResponseEntity.ok(response);
    }
}
