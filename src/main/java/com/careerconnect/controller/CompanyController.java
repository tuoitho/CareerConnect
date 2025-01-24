package com.careerconnect.controller;

import com.careerconnect.dto.ApiResponse;
import com.careerconnect.dto.request.RegisterCompanyRequest;
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
    @GetMapping("/company")
    public ResponseEntity<?> getCompany() {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Company retrieved successfully")
                .result(companyService.getCurrentCompany(authenticationHelper.getUserId()))
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/company")
    public ResponseEntity<?> registerCompany(@RequestBody RegisterCompanyRequest registerCompanyRequest) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Company registered successfully")
                .result(companyService.registerCompany(authenticationHelper.getUserId(), registerCompanyRequest))
                .build();
        return ResponseEntity.ok(response);
    }
}
