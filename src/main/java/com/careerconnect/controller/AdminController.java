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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin", description = "API quản trị hệ thống dành cho quản trị viên")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserService userService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final CoinRechargeService coinRechargeService;
    private final AdminService adminService;

    // Quản lý người dùng
    @Operation(summary = "Danh sách người dùng", description = "API lấy danh sách tất cả người dùng trong hệ thống")
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

    @Operation(summary = "Khóa người dùng", description = "API khóa tài khoản người dùng")
    @PutMapping("/users/{userId}/lock")
    public ResponseEntity<ApiResp<String>> lockUser(@PathVariable Long userId) {
        adminService.lockUser(userId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("User locked successfully")
                .build());
    }

    @Operation(summary = "Mở khóa người dùng", description = "API mở khóa tài khoản người dùng")
    @PutMapping("/users/{userId}/unlock")
    public ResponseEntity<ApiResp<String>> unlockUser(@PathVariable Long userId) {
        adminService.unlockUser(userId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("User unlocked successfully")
                .build());
    }

    @Operation(summary = "Chi tiết người dùng", description = "API xem thông tin chi tiết của người dùng")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResp<AdminUserResponse>> getUserDetail(@PathVariable Long userId) {
        AdminUserResponse user = adminService.getUserDetail(userId);
        return ResponseEntity.ok(ApiResp.<AdminUserResponse>builder()
                .result(user)
                .message("User detail retrieved successfully")
                .build());
    }

    // Quản lý công ty
    @Operation(summary = "Danh sách công ty", description = "API lấy danh sách tất cả công ty trong hệ thống")
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

    @Operation(summary = "Phê duyệt công ty", description = "API phê duyệt một công ty mới đăng ký")
    @PutMapping("/companies/{companyId}/approve")
    public ResponseEntity<ApiResp<String>> approveCompany(@PathVariable Long companyId) {
        adminService.approveCompany(companyId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Company approved successfully")
                .build());
    }

    @Operation(summary = "Khóa công ty", description = "API khóa hoạt động của một công ty")
    @PutMapping("/companies/{companyId}/lock")
    public ResponseEntity<ApiResp<String>> lockCompany(@PathVariable Long companyId) {
        adminService.lockCompany(companyId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Company locked successfully")
                .build());
    }

    @Operation(summary = "Mở khóa công ty", description = "API mở khóa hoạt động của một công ty")
    @PutMapping("/companies/{companyId}/unlock")
    public ResponseEntity<ApiResp<String>> unlockCompany(@PathVariable Long companyId) {
        adminService.unlockCompany(companyId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Company unlocked successfully")
                .build());
    }

    // Quản lý tin tuyển dụng
    @Operation(summary = "Danh sách tin tuyển dụng", description = "API lấy danh sách tất cả tin tuyển dụng trong hệ thống")
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

    @Operation(summary = "Phê duyệt tin tuyển dụng", description = "API phê duyệt một tin tuyển dụng mới đăng")
    @PutMapping("/jobs/{jobId}/approve")
    public ResponseEntity<ApiResp<String>> approveJob(@PathVariable Long jobId) {
        adminService.approveJob(jobId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Job approved successfully")
                .build());
    }

    @Operation(summary = "Ẩn tin tuyển dụng", description = "API ẩn một tin tuyển dụng khỏi hệ thống")
    @PutMapping("/jobs/{jobId}/hide")
    public ResponseEntity<ApiResp<String>> hideJob(@PathVariable Long jobId) {
        adminService.hideJob(jobId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Job hidden successfully")
                .build());
    }

    @Operation(summary = "Hiện tin tuyển dụng", description = "API hiện lại một tin tuyển dụng đã bị ẩn")
    @PutMapping("/jobs/{jobId}/show")
    public ResponseEntity<ApiResp<String>> showJob(@PathVariable Long jobId) {
        adminService.showJob(jobId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Job shown successfully")
                .build());
    }

    // Quản lý giao dịch
    @Operation(summary = "Danh sách giao dịch", description = "API lấy danh sách tất cả giao dịch nạp xu trong hệ thống")
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

    @Operation(summary = "Xác nhận giao dịch", description = "API xác nhận một giao dịch nạp xu thành công")
    @PutMapping("/transactions/{transactionId}/confirm")
    public ResponseEntity<ApiResp<String>> confirmTransaction(@PathVariable Long transactionId) {
        coinRechargeService.completeRecharge(transactionId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Transaction confirmed successfully")
                .build());
    }

    @Operation(summary = "Hủy giao dịch", description = "API hủy một giao dịch nạp xu")
    @PutMapping("/transactions/{transactionId}/cancel")
    public ResponseEntity<ApiResp<String>> cancelTransaction(@PathVariable Long transactionId) {
        coinRechargeService.failRecharge(transactionId);
        return ResponseEntity.ok(ApiResp.<String>builder()
                .message("Transaction cancelled successfully")
                .build());
    }


    @Operation(summary = "Thống kê dashboard", description = "API lấy thống kê tổng quan cho dashboard quản trị")
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