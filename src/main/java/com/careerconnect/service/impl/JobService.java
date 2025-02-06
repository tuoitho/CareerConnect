package com.careerconnect.service.impl;

import com.careerconnect.dto.common.PaginatedResponse;
import com.careerconnect.dto.request.CreateJobRequest;
import com.careerconnect.dto.response.CreateJobResponse;
import com.careerconnect.dto.response.MemberResponse;
import com.careerconnect.dto.response.PostedJobDetailResponse;
import com.careerconnect.entity.*;
import com.careerconnect.enums.JobTypeEnum;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.repository.ApplicationRepo;
import com.careerconnect.repository.CompanyRepo;
import com.careerconnect.repository.JobRepo;
import com.careerconnect.repository.UserRepository;
import com.careerconnect.service.PaginationService;
import com.careerconnect.util.AuthenticationHelper;
import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepo jobRepository;
    private final UserRepository userRepository;
    private final ApplicationRepo applicationRepo;
    private final PaginationService paginationService;
    private final CompanyRepo companyRepo;

    //create
    public CreateJobResponse createJob(Long userId, CreateJobRequest req) {
        //get company from recruiter
        Recruiter recruiter = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Company company = recruiter.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }
        //create job
        Job job = Job.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .location(req.getLocation())
                .type(JobTypeEnum.valueOf(req.getType()))
                .minSalary(req.getMinSalary())
                .maxSalary(req.getMaxSalary())
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .deadline(req.getDeadline())
                .experience(req.getExperience())
                .category(req.getCategory())
                .company(company)
                .build();
        Job savedJob = jobRepository.save(job);
        return CreateJobResponse.builder()
                .jobId(savedJob.getJobId())
                .title(savedJob.getTitle())
                .description(savedJob.getDescription())
                .location(savedJob.getLocation())
                .type(savedJob.getType())
                .minSalary(savedJob.getMinSalary())
                .maxSalary(savedJob.getMaxSalary())
                .created(savedJob.getCreated())
                .updated(savedJob.getUpdated())
                .deadline(savedJob.getDeadline())
                .experience(savedJob.getExperience())
                .category(savedJob.getCategory())
                .active(savedJob.isActive())
                .build();
    }

    public CreateJobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
        return CreateJobResponse.builder()
                .jobId(job.getJobId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .type(job.getType())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .created(job.getCreated())
                .updated(job.getUpdated())
                .deadline(job.getDeadline())
                .experience(job.getExperience())
                .category(job.getCategory())
                .active(job.isActive())
                .build();
    }

    public PostedJobDetailResponse getPostedJobDetail(Long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
        List<Application> applications = applicationRepo.findAllByJob(job);
        List<PostedJobDetailResponse.ApplicationWithCandidate> applicationWithCandidates = applications.stream().map(application -> {
            Candidate candidate = application.getCandidate();
            return PostedJobDetailResponse.ApplicationWithCandidate.builder()
                            .applicationId(application.getApplicationId())
                            .candidateName(candidate.getFullname())
                            .appliedAt(application.getAppliedAt())
                            .processed(application.isProcessed())
                            .build();
        }).toList();
        PostedJobDetailResponse response = new PostedJobDetailResponse();
        BeanUtils.copyProperties(job, response);
        response.setApplications(applicationWithCandidates);
        return response;
    }

    public CreateJobResponse updateJob(Long userId, Long id, CreateJobRequest job) {
        Job jobToUpdate = jobRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
        //get company from recruiter
        Recruiter recruiter = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Company company = recruiter.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }
        if (!jobToUpdate.getCompany().equals(company)) {
            throw new AppException(ErrorCode.NOT_PERMITTED);
        }
        jobToUpdate.setTitle(job.getTitle());
        jobToUpdate.setDescription(job.getDescription());
        jobToUpdate.setLocation(job.getLocation());
        jobToUpdate.setType(JobTypeEnum.valueOf(job.getType()));
        jobToUpdate.setMinSalary(job.getMinSalary());
        jobToUpdate.setMaxSalary(job.getMaxSalary());
        jobToUpdate.setUpdated(LocalDateTime.now());
        jobToUpdate.setDeadline(job.getDeadline());
        jobToUpdate.setExperience(job.getExperience());
        jobToUpdate.setCategory(job.getCategory());
        Job updatedJob = jobRepository.save(jobToUpdate);
        return CreateJobResponse.builder()
                .jobId(updatedJob.getJobId())
                .title(updatedJob.getTitle())
                .description(updatedJob.getDescription())
                .location(updatedJob.getLocation())
                .type(updatedJob.getType())
                .minSalary(updatedJob.getMinSalary())
                .maxSalary(updatedJob.getMaxSalary())
                .created(updatedJob.getCreated())
                .updated(updatedJob.getUpdated())
                .deadline(updatedJob.getDeadline())
                .experience(updatedJob.getExperience())
                .category(updatedJob.getCategory())
                .active(updatedJob.isActive())
                .build();
    }

    public void deleteJob(Long userId, Long id) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
        //get company from recruiter
        Recruiter recruiter = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Company company = recruiter.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }
        if (!job.getCompany().equals(company)) {
            throw new AppException(ErrorCode.NOT_PERMITTED);
        }
        if (applicationRepo.existsByJob(job)) {
            job.setActive(false);
            job.setUpdated(LocalDateTime.now());
            jobRepository.save(job);
            return;
        }
        jobRepository.delete(job);
    }

    public PaginatedResponse<CreateJobResponse> getJobs(Long userId,int page, int size) {
        Recruiter recruiter = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Company company = recruiter.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobs = jobRepository.findAllByCompany(company, pageable);
        Logger.log("Jobs: " + jobs.getContent());
        return paginationService.paginate(jobs, j -> CreateJobResponse.builder()
                .jobId(j.getJobId())
                .title(j.getTitle())
                .description(j.getDescription())
                .location(j.getLocation())
                .type(JobTypeEnum.valueOf(j.getType().name()))
                .minSalary(j.getMinSalary())
                .maxSalary(j.getMaxSalary())
                .created(j.getCreated())
                .updated(j.getUpdated())
                .deadline(j.getDeadline())
                .experience(j.getExperience())
                .category(j.getCategory())
                .active(j.isActive())
                .build());
    }

    public PaginatedResponse<CreateJobResponse> getCompanyJobs(Long companyId, int page, int size) {
        Company company = companyRepo.findById(companyId).orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobs = jobRepository.findAllByCompanyAndActiveTrue(company, pageable);
        return paginationService.paginate(jobs, j -> CreateJobResponse.builder()
                .jobId(j.getJobId())
                .title(j.getTitle())
                .description(j.getDescription())
                .location(j.getLocation())
                .type(JobTypeEnum.valueOf(j.getType().name()))
                .minSalary(j.getMinSalary())
                .maxSalary(j.getMaxSalary())
                .created(j.getCreated())
                .updated(j.getUpdated())
                .deadline(j.getDeadline())
                .experience(j.getExperience())
                .category(j.getCategory())
                .active(j.isActive())
                .build());
    }
}
