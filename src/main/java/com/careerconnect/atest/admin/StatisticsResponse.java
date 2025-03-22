package com.careerconnect.atest.admin;

import lombok.Data;

import java.util.Map;

@Data
public class StatisticsResponse {
    private long totalUsers;
    private long activeUsers;
    private long candidateCount;
    private long recruiterCount;
    private long totalCompanies;
    private long verifiedCompanies;
    private long totalJobs;
    private long activeJobs;
    private long totalTransactions;
    private double totalRevenue;
    private Map<String, Integer> userRegistrationTrend;
    private Map<String, Integer> jobPostingTrend;
    private Map<String, Double> revenueTrend;
}