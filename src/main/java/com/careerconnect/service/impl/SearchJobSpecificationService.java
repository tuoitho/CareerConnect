package com.careerconnect.service.impl;

import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.common.PaginatedResponse;
import com.careerconnect.dto.response.JobResponse;
import com.careerconnect.entity.Job;
import com.careerconnect.repository.JobRepo;
import com.careerconnect.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchJobSpecificationService {
    private final JobRepo jobRepo;
    private final PaginationService paginationService;

    public Specification<Job> buildSpecification(
            String keyword,
            String area,
            String jobType,
            String experience,
            String category,
            String minSalary,
            String maxSalary
    ) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (keyword != null && !keyword.isEmpty()) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.like(root.get("title"), "%" + keyword + "%"));
            }
            if (area != null && !area.isEmpty()) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("area"), area));
            }
            if (jobType != null && !jobType.isEmpty()) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("type"), jobType));
            }
            if (experience != null && !experience.isEmpty()) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("experience"), experience));
            }
            if (category != null && !category.isEmpty()) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("category"), category));
            }
            if (minSalary != null && !minSalary.isEmpty()) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.greaterThanOrEqualTo(root.get("minSalary"), minSalary));
            }
            if (maxSalary != null && !maxSalary.isEmpty()) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.lessThanOrEqualTo(root.get("maxSalary"), maxSalary));
            }

            return predicates;
        };
    }
    public PaginatedResponse<JobResponse> searchJobsWithSpec(Specification<Job> spec, Pageable pageable) {
        Page<Job> jobs = jobRepo.findAll(spec, pageable);
        return paginationService.paginate(jobs, j -> JobResponse.builder()
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