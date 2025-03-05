package com.careerconnect.service.impl;

import com.careerconnect.dto.response.SavedJobResponseDTO;
import com.careerconnect.entity.Candidate;
import com.careerconnect.entity.Job;

import com.careerconnect.entity.SavedJob;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.CandidateRepo;
import com.careerconnect.repository.JobRepo;
import com.careerconnect.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedJobService {
    private final SavedJobRepository savedJobRepository;
    private final CandidateRepo candidateRepository;
    private final JobRepo jobRepository;

    @Transactional
    public SavedJobResponseDTO saveJob(Long candidateId, Long jobId) {
        Candidate candidate = candidateRepository.findById(candidateId)
            .orElseThrow(() -> new ResourceNotFoundException(Candidate.class, candidateId));
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new ResourceNotFoundException(Job.class, jobId));

        if (savedJobRepository.findByCandidateUserIdAndJobJobId(candidateId, jobId).isPresent()) {
            throw new AppException(ErrorCode.JOB_ALREADY_SAVED);
        }

        SavedJob savedJob = new SavedJob();
        savedJob.setCandidate(candidate);
        savedJob.setJob(job);
        savedJob = savedJobRepository.save(savedJob);

        return mapToDTO(savedJob);
    }

    public List<SavedJobResponseDTO> getSavedJobs(Long candidateId) {
        List<SavedJob> savedJobs = savedJobRepository.findByCandidateUserId(candidateId);
        return savedJobs.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void unsaveJob(Long candidateId, Long jobId) {
//        savedJobRepository.findByCandidateUserIdAndJobJobId(candidateId, jobId)
//            .orElseThrow(() -> new ResourceNotFoundException(SavedJob.class, jobId));
//        savedJobRepository.deleteByCandidateUserIdAndJobJobId(candidateId, jobId);
//        Job job = jobRepository.findById(jobId)
//            .orElseThrow(() -> new ResourceNotFoundException(Job.class, jobId));
        SavedJob savedJob = savedJobRepository.findByCandidateUserIdAndJobJobId(candidateId, jobId)
            .orElseThrow(() -> new ResourceNotFoundException(SavedJob.class, jobId));
        savedJobRepository.delete(savedJob);
    }

    private SavedJobResponseDTO mapToDTO(SavedJob savedJob) {
        SavedJobResponseDTO dto = new SavedJobResponseDTO();
        dto.setId(savedJob.getSavedJobId());
        dto.setJobId(savedJob.getJob().getJobId());
        dto.setJobTitle(savedJob.getJob().getTitle());
        dto.setCompanyName(savedJob.getJob().getCompany().getName());
        dto.setCompanyLogo(savedJob.getJob().getCompany().getLogo());
        dto.setSavedAt(savedJob.getSavedAt());
        return dto;
    }
}