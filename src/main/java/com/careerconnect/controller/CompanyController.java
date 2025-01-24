package com.careerconnect.controller;

import com.careerconnect.dto.ApiResponse;
import com.careerconnect.service.impl.CompanyService;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;
    private final AuthenticationHelper authenticationHelper;
    @GetMapping("/company")
    public ResponseEntity<?> getCompany() {
        ApiResponse<?> response = ApiResponse.builder()
                .result(companyService.getCurrentCompany(authenticationHelper.getUserId()))
                .build();
        return ResponseEntity.ok(response);
    }
}
