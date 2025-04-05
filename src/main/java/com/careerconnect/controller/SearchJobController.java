package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.common.PaginatedResponse;
import com.careerconnect.dto.response.JobResponse;
import com.careerconnect.service.impl.SearchJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(ApiEndpoint.PREFIX+"/search")
@Controller
@RequiredArgsConstructor
@PreAuthorize(SecurityEndpoint.CANDIDATE)
public class SearchJobController {
    private final SearchJobService searchJobService;

    @GetMapping("/search-with-filter")
    public ResponseEntity<?> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        ApiResp<PaginatedResponse<JobResponse>> response = ApiResp.<PaginatedResponse<JobResponse>>builder()
                .result(searchJobService.searchJobs(keyword, area, jobType, experience, category, minSalary, maxSalary, page, size))
                .build();
        return ResponseEntity.ok(response);
    }
}
