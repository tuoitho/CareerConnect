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
import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
    @Transactional
    public void unlockCompany(Long companyId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(Company.class, companyId));
        company.setActive(true);
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
    @Transactional
    public void showJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException(Job.class, jobId));
        job.setActive(true);
        jobRepository.save(job);
    }

    // Thống kê
    // Statistics
    public StatisticsResponse getStatistics(String fromDate, String toDate, String groupBy) {
        LocalDate from = fromDate != null && !fromDate.isEmpty() ? LocalDate.parse(fromDate) : LocalDate.now().minusMonths(1);
        LocalDate to = toDate != null && !toDate.isEmpty() ? LocalDate.parse(toDate) : LocalDate.now();
        StatisticsResponse response = new StatisticsResponse();
        response.setTotalUsers(userRepository.count());
        response.setActiveUsers(userRepository.countByLockedFalse());
        response.setCandidateCount(userRepository.countByRole_RoleName(RoleEnum.CANDIDATE));
        response.setRecruiterCount(userRepository.countByRole_RoleName(RoleEnum.RECRUITER));
        response.setTotalCompanies(companyRepo.count());
        response.setVerifiedCompanies(companyRepo.countByApprovedTrue());
        response.setTotalJobs(jobRepository.count());
        response.setTotalTransactions(coinRechargeRepository.count());
        response.setTotalRevenue(coinRechargeRepository.sumAmountPaidByStatusAndDateRange("SUCCESS",
                from.atStartOfDay(), to.plusDays(1).atStartOfDay()));

        Map<String, Integer> userRegistrationTrend = new HashMap<>();
        Map<String, Integer> jobPostingTrend = new HashMap<>();
        Map<String, Double> revenueTrend = new HashMap<>();

        switch (groupBy.toLowerCase()) {
            case "month":
                userRegistrationTrend = userRepository.countUserRegistrationsByMonth(from, to);
                jobPostingTrend = jobRepository.countJobPostingsByMonth(from, to);
                revenueTrend = coinRechargeRepository.sumRevenueByMonth(from, to);
                break;
            case "week":
                userRegistrationTrend = userRepository.countUserRegistrationsByWeek(from, to);
                jobPostingTrend = jobRepository.countJobPostingsByWeek(from, to);
                revenueTrend = coinRechargeRepository.sumRevenueByWeek(from, to);
                break;
            case "day":
            default:
                userRegistrationTrend = userRepository.countUserRegistrationsByDay(from, to);
                jobPostingTrend = jobRepository.countJobPostingsByDay(from, to);
                revenueTrend = coinRechargeRepository.sumRevenueByDay(from, to);
                break;
        }

        response.setUserRegistrationTrend(userRegistrationTrend);
        response.setJobPostingTrend(jobPostingTrend);
        response.setRevenueTrend(revenueTrend);
        return response;
    }
}