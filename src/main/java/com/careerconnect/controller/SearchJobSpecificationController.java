package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.common.PaginatedResponse;
import com.careerconnect.dto.response.JobResponse;
import com.careerconnect.entity.Job;
import com.careerconnect.service.impl.SearchJobSpecificationService;
import com.cloudinary.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(ApiEndpoint.PREFIX + "/search-spec")
@Controller
@RequiredArgsConstructor
@PreAuthorize(SecurityEndpoint.CANDIDATE)
public class SearchJobSpecificationController {
    private final SearchJobSpecificationService searchJobSpecificationService;

    @GetMapping("/search-with-spec")
    public ResponseEntity<?> searchJobsWithSpec(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String minSalary,
            @RequestParam(required = false) String maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Specification<Job> spec = searchJobSpecificationService.buildSpecification(keyword, area, jobType, experience, category, minSalary, maxSalary);
        ApiResp<?> response = ApiResp.builder().result(
                        searchJobSpecificationService.searchJobsWithSpec(spec, PageRequest.of(page, size)))
                .build();

        return ResponseEntity.ok(response);
    }
}