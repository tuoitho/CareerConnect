// src/main/java/com/careerconnect/controller/AdminController.java
package com.careerconnect.controller;

import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.response.AdminJobResponse;
import com.careerconnect.service.impl.AdminService;
import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.common.PaginatedResponse;
import com.careerconnect.dto.response.*;
import com.careerconnect.service.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoint.PREFIX + "/admin")
@RequiredArgsConstructor
@PreAuthorize(SecurityEndpoint.ADMIN)
public class AdminController {

    private final UserService userService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final CoinRechargeService coinRechargeService;
    private final AdminService adminService;

    // Quản lý người dùng
    @GetMapping("/users")
    public ResponseEntity<ApiResp<PaginatedResponse<AdminUserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<AdminUserResponse> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResp.<PaginatedResponse<AdminUserResponse>>builder()
                .result(users)
                .message("Users retrieved successfully")
                .build());
    }

    @PutMapping("/users/{userId}/lock")
    public ResponseEntity<ApiResp<String>> lockUser(@PathVariable Long userId) {
        adminService.lockUser(userId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("User locked successfully")
                .build());
    }

    @PutMapping("/users/{userId}/unlock")
    public ResponseEntity<ApiResp<String>> unlockUser(@PathVariable Long userId) {
        adminService.unlockUser(userId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("User unlocked successfully")
                .build());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResp<AdminUserResponse>> getUserDetail(@PathVariable Long userId) {
        AdminUserResponse user = adminService.getUserDetail(userId);
        return ResponseEntity.ok(ApiResp.<AdminUserResponse>builder()
                .result(user)
                .message("User detail retrieved successfully")
                .build());
    }

    // Quản lý công ty
    @GetMapping("/companies")
    public ResponseEntity<ApiResp<PaginatedResponse<AdminCompanyResponse>>> getAllCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<AdminCompanyResponse> companies = adminService.getAllCompanies(pageable);
        return ResponseEntity.ok(ApiResp.<PaginatedResponse<AdminCompanyResponse>>builder()
                .result(companies)
                .message("Companies retrieved successfully")
                .build());
    }

    @PutMapping("/companies/{companyId}/approve")
    public ResponseEntity<ApiResp<String>> approveCompany(@PathVariable Long companyId) {
        adminService.approveCompany(companyId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Company approved successfully")
                .build());
    }

    @PutMapping("/companies/{companyId}/lock")
    public ResponseEntity<ApiResp<String>> lockCompany(@PathVariable Long companyId) {
        adminService.lockCompany(companyId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Company locked successfully")
                .build());
    }
    @PutMapping("/companies/{companyId}/unlock")
    public ResponseEntity<ApiResp<String>> unlockCompany(@PathVariable Long companyId) {
        adminService.unlockCompany(companyId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Company unlocked successfully")
                .build());
    }

    // Quản lý tin tuyển dụng
    @GetMapping("/jobs")
    public ResponseEntity<ApiResp<PaginatedResponse<AdminJobResponse>>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<AdminJobResponse> jobs = adminService.getAllJobs(pageable);
        return ResponseEntity.ok(ApiResp.<PaginatedResponse<AdminJobResponse>>builder()
                .result(jobs)
                .message("Jobs retrieved successfully")
                .build());
    }

    @PutMapping("/jobs/{jobId}/approve")
    public ResponseEntity<ApiResp<String>> approveJob(@PathVariable Long jobId) {
        adminService.approveJob(jobId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Job approved successfully")
                .build());
    }

    @PutMapping("/jobs/{jobId}/hide")
    public ResponseEntity<ApiResp<String>> hideJob(@PathVariable Long jobId) {
        adminService.hideJob(jobId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Job hidden successfully")
                .build());
    }
    @PutMapping("/jobs/{jobId}/show")
    public ResponseEntity<ApiResp<String>> showJob(@PathVariable Long jobId) {
        adminService.showJob(jobId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Job shown successfully")
                .build());
    }

    // Quản lý giao dịch
    @GetMapping("/transactions")
    public ResponseEntity<ApiResp<PaginatedResponse<CoinRechargeResponse>>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<CoinRechargeResponse> transactions = coinRechargeService.getAllRechargeHistory(pageable);
        return ResponseEntity.ok(ApiResp.<PaginatedResponse<CoinRechargeResponse>>builder()
                .result(transactions)
                .message("Transactions retrieved successfully")
                .build());
    }

    @PutMapping("/transactions/{transactionId}/confirm")
    public ResponseEntity<ApiResp<String>> confirmTransaction(@PathVariable Long transactionId) {
        coinRechargeService.completeRecharge(transactionId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Transaction confirmed successfully")
                .build());
    }

    @PutMapping("/transactions/{transactionId}/cancel")
    public ResponseEntity<ApiResp<String>> cancelTransaction(@PathVariable Long transactionId) {
        coinRechargeService.failRecharge(transactionId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Transaction cancelled successfully")
                .build());
    }


    // Dashboard APIs
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResp<StatisticsResponse>> getDashboardStats(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "day") String groupBy) {

        StatisticsResponse stats = adminService.getStatistics(fromDate, toDate, groupBy);
        return ResponseEntity.ok(ApiResp.<StatisticsResponse>builder()
                .result(stats)
                .message("Dashboard statistics retrieved successfully")
                .build());
    }


}