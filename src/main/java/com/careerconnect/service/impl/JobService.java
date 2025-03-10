package com.careerconnect.service.impl;

import com.careerconnect.event.JobAlertProducer;
import com.careerconnect.event.JobApplicationProducer;
import com.careerconnect.repository.SavedJobRepository;
import com.careerconnect.dto.common.PaginatedResponse;
import com.careerconnect.dto.request.ApplyJobRequest;
import com.careerconnect.dto.request.CreateJobRequest;
import com.careerconnect.dto.response.*;
import com.careerconnect.entity.*;
import com.careerconnect.enums.JobTypeEnum;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.*;
import com.careerconnect.service.PaginationService;
import com.careerconnect.util.Logger;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final CandidateRepo candidateRepo;
    private final CvRepo cvRepo;
    private final SavedJobRepository savedJobRepository;
    private final JobAlertProducer jobAlertProducer;
    private final JobApplicationProducer jobApplicationProducer;

    //apply job
    @Transactional
    public void applyJob(Long canId, ApplyJobRequest request) {

        Candidate candidate = candidateRepo.findById(canId).orElseThrow(() -> new ResourceNotFoundException(Candidate.class, canId));
        Job job = jobRepository.findById(request.getJobId()).orElseThrow(() -> new ResourceNotFoundException(Job.class, request.getJobId()));
        //check if applied
        if (applicationRepo.existsByCandidateAndJob(candidate, job)) {
            throw new AppException(ErrorCode.ALREADY_APPLIED);
        }
        //check if deadline
        if (job.getDeadline().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.JOB_DEADLINE);
        }
        CV cv = cvRepo.findById(request.getCvId()).orElseThrow(() -> new ResourceNotFoundException(CV.class, request.getCvId()));

        Application application = Application.builder()
                .coverLetter(request.getCoverLetter())
                .candidate(candidate)
                .job(job)
                .build();
        ApplicationCV applicationCV = ApplicationCV.builder()
                .name(cv.getName())
                .path(cv.getPath())
                .build();
        application.assignCV(applicationCV);

        application=applicationRepo.save(application);
        jobApplicationProducer.notifyJobApplication(application);

    }
    //create
    public CreateJobResponse createJob(Long userId, CreateJobRequest req) {
        //get company from recruiter
        Recruiter recruiter = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, userId));
        Company company = recruiter.getCompany();
        if (company == null) {
//            throw new AppException(ErrorCode.NO_LINKED_COMPANY);
                throw new AppException(ErrorCode.NO_LINKED_COMPANY);        
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

        // Gửi thông điệp vào RabbitMQ
        jobAlertProducer.notifySubscribers(job);
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

    //dành cho candidate
    public JobDetailResponse getJobDetailById(Long candidateId, Long id) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Job.class, id));
        boolean isApplied = applicationRepo.existsByCandidate_userIdAndJob_jobId(candidateId, id);
        boolean isSaved = savedJobRepository.existsByCandidate_userIdAndJob_jobId(candidateId, id);
        return JobDetailResponse.builder()
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
                .companyId(job.getCompany().getCompanyId())
                .applied(isApplied)
                .saved(isSaved)
                .build();
    }

    public PostedJobDetailResponse getPostedJobDetail(Long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException(Job.class, jobId));
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
        Job jobToUpdate = jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Job.class, id));
        //get company from recruiter
        Recruiter recruiter = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, userId));
        Company company = recruiter.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.NO_LINKED_COMPANY);
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
        Job job = jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Job.class, id));
        //get company from recruiter
        Recruiter recruiter = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, userId));
        Company company = recruiter.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.NO_LINKED_COMPANY);
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
        Recruiter recruiter = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, userId));
        Company company = recruiter.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.NO_LINKED_COMPANY);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobs = jobRepository.findAllByCompany(company, pageable);
        return paginationService.paginate(jobs, j -> CreateJobResponse.builder()
                .jobId(j.getJobId())
                .title(j.getTitle())
                .description(j.getDescription())
                .location(j.getLocation())
                .type(JobTypeEnum.valueOf(j.getType().name()))
                .experience(j.getExperience())
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
        Company company = companyRepo.findById(companyId).orElseThrow(() -> new ResourceNotFoundException(Company.class, companyId));
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

    public PaginatedResponse<AppliedJobResponse> getAppliedJobs(Long candidateId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<Application> applications = applicationRepo.findAllByCandidate_userId(candidateId, pageable);
        return paginationService.paginate(applications, application -> {
            Job job = application.getJob();
            return AppliedJobResponse.builder()
                    .applicationId(application.getApplicationId())
                    .coverLetter(application.getCoverLetter())
                    .appliedAt(application.getAppliedAt())
                    .job(AppliedJobResponse.AppliedJob.builder()
                            .jobId(job.getJobId())
                            .title(job.getTitle())
                            .image(job.getCompany().getLogo())
                            .build())
                    .build();
        });

    }

    public PaginatedResponse<SearchJobItemResponse> searchJobs(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobs = jobRepository.findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, pageable);
        return paginationService.paginate(jobs, job -> SearchJobItemResponse.builder()
                .jobId(job.getJobId())
                .title(job.getTitle())
                .location(job.getLocation())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .companyName(job.getCompany().getName())
                .companyLogo(job.getCompany().getLogo())
                .build());
    }
}
