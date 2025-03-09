package com.careerconnect.service.impl;

import com.careerconnect.dto.common.PaginatedResponse;
import com.careerconnect.dto.response.JobResponse;
import com.careerconnect.entity.Job;
import com.careerconnect.enums.ExpEnum;
import com.careerconnect.enums.JobTypeEnum;
import com.careerconnect.repository.JobRepo;
import com.careerconnect.service.PaginationService;
import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchJobService {
    private final JobRepo jobRepo;
    private final PaginationService paginationService;


    public PaginatedResponse<JobResponse> searchJobs(
            String keyword,
            String area,
            String jobType,
            String experience,
            String category,
            Double minSalary,
            Double maxSalary,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Logger.log(jobType);
        Page<Job> jobs = jobRepo.searchJobs(
                keyword.isEmpty() ? null : keyword,
                area.isEmpty() ? null : area,
                jobType.isEmpty()?null:JobTypeEnum.valueOf(jobType),
                experience.isEmpty()?null:ExpEnum.valueOf(experience),
                category.isEmpty() ? null : category,
                minSalary,
                maxSalary,
                pageable
        );
        return  paginationService.paginate(jobs, j-> JobResponse.builder()
                .jobId(j.getJobId())
                .title(j.getTitle())
                .description(j.getDescription())
                .area(j.getArea())
                .location(j.getLocation())
                .jobType(j.getType().name())
                .experience(j.getExperience().name())
                .minSalary(j.getMinSalary())
                .maxSalary(j.getMaxSalary())
                .created(j.getCreated())
                .deadline(j.getDeadline())
                .category(j.getCategory())
                .active(j.isActive())
                .companyName(j.getCompany().getName())
                .companyLogo(j.getCompany().getLogo())
                .build());
    }
}
