package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.common.PaginatedResponse;
import com.careerconnect.dto.response.JobResponse;
import com.careerconnect.service.impl.SearchJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Job Search", description = "API tìm kiếm công việc với các bộ lọc")
@SecurityRequirement(name = "bearerAuth")
public class SearchJobController {
    private final SearchJobService searchJobService;

    @Operation(summary = "Tìm kiếm công việc", description = "API tìm kiếm công việc với nhiều tiêu chí lọc khác nhau")
    @GetMapping("/search-with-filter")
    public ResponseEntity<?> searchJobs(
            @Parameter(description = "Từ khóa tìm kiếm") 
            @RequestParam(required = false) String keyword,
            @Parameter(description = "Khu vực làm việc") 
            @RequestParam(required = false) String area,
            @Parameter(description = "Loại công việc (Full-time, Part-time, Remote,...)") 
            @RequestParam(required = false) String jobType,
            @Parameter(description = "Kinh nghiệm yêu cầu") 
            @RequestParam(required = false) String experience,
            @Parameter(description = "Danh mục ngành nghề") 
            @RequestParam(required = false) String category,
            @Parameter(description = "Mức lương tối thiểu") 
            @RequestParam(required = false) Double minSalary,
            @Parameter(description = "Mức lương tối đa") 
            @RequestParam(required = false) Double maxSalary,
            @Parameter(description = "Số trang (bắt đầu từ 0)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng kết quả mỗi trang") 
            @RequestParam(defaultValue = "5") int size
    ) {
        ApiResp<PaginatedResponse<JobResponse>> response = ApiResp.<PaginatedResponse<JobResponse>>builder()
                .result(searchJobService.searchJobs(keyword, area, jobType, experience, category, minSalary, maxSalary, page, size))
                .build();
        return ResponseEntity.ok(response);
    }
}
