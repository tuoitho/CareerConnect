// src/main/java/com/careerconnect/dto/response/AdminStatsResponse.java
package com.careerconnect.atest.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminStatsResponse {
    private long totalUsers;
    private long activeUsers;
    private long candidates;
    private long recruiters;
    private long totalCompanies;
    private long totalJobs;
    private double totalRevenue;
}