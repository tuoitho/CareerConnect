// src/main/java/com/careerconnect/service/impl/AdminService.java
package com.careerconnect.atest.admin;

import com.careerconnect.dto.common.PaginatedResponse;
import com.careerconnect.dto.response.*;
import com.careerconnect.entity.Company;
import com.careerconnect.entity.Job;
import com.careerconnect.entity.User;
import com.careerconnect.enums.RoleEnum;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.*;
import com.careerconnect.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CompanyRepo companyRepo;
    private final JobRepo jobRepository;
    private final CoinRechargeRepository coinRechargeRepository;
    private final PaginationService paginationService;

    // Quản lý người dùng
    public PaginatedResponse<AdminUserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return paginationService.paginate(users, user -> AdminUserResponse.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullname())
                .role(user.getRole().getRoleName().name())
                .isLocked(!user.getActive())
                .createdAt(user.getCreatedAt().toString())
                .build());
    }

    @Transactional
    public void lockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userId));
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void unlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userId));
        user.setActive(true);
        userRepository.save(user);
    }

    public AdminUserResponse getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userId));
        return AdminUserResponse.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullname())
                .role(user.getRole().getRoleName().name())
                .isLocked(!user.getActive())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }

    // Quản lý công ty
    public PaginatedResponse<AdminCompanyResponse> getAllCompanies(Pageable pageable) {
        Page<Company> companies = companyRepo.findAll(pageable);
        return paginationService.paginate(companies, company -> AdminCompanyResponse.builder()
                .id(company.getCompanyId())
                .name(company.getName())
                .logo(company.getLogo())
                .isApproved(company.isApproved())
                .isLocked(!company.getActive())
                .createdAt(company.getCreatedAt().toString())
                .build());
    }

    @Transactional
    public void approveCompany(Long companyId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(Company.class, companyId));
        company.setApproved(true);
        companyRepo.save(company);
    }

    @Transactional
    public void lockCompany(Long companyId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(Company.class, companyId));
        company.setActive(false);
        companyRepo.save(company);
    }

    // Quản lý tin tuyển dụng
    public PaginatedResponse<AdminJobResponse> getAllJobs(Pageable pageable) {
        Page<Job> jobs = jobRepository.findAll(pageable);
        return paginationService.paginate(jobs, job -> AdminJobResponse.builder()
                .id(job.getJobId())
                .title(job.getTitle())
                .companyName(job.getCompany().getName())
                .isApproved(job.isApproved())
                .isVisible(job.isActive())
                .createdAt(job.getCreated().toString())
                .build());
    }

    @Transactional
    public void approveJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException(Job.class, jobId));
        job.setApproved(true);
        jobRepository.save(job);
    }

    @Transactional
    public void hideJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException(Job.class, jobId));
        job.setActive(false);
        jobRepository.save(job);
    }

    // Thống kê
    public AdminStatsResponse getStatistics(String startDate, String endDate) {
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusMonths(1);
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now();

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByLockedFalse();
        long candidates = userRepository.countByRole_RoleName(RoleEnum.CANDIDATE);
        long recruiters = userRepository.countByRole_RoleName(RoleEnum.RECRUITER);
        long totalCompanies = companyRepo.count();
        long totalJobs = jobRepository.count();
        double totalRevenue = coinRechargeRepository.sumAmountPaidByStatusAndDateRange("SUCCESS", start, end);

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .candidates(candidates)
                .recruiters(recruiters)
                .totalCompanies(totalCompanies)
                .totalJobs(totalJobs)
                .totalRevenue(totalRevenue)
                .build();
    }
}