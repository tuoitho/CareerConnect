package com.careerconnect.service.impl;

import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.request.InterviewRequest;
import com.careerconnect.dto.response.InterviewResponse;
import com.careerconnect.entity.Application;
import com.careerconnect.entity.Recruiter;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.entity.InterviewRoom;
import com.careerconnect.enums.InterviewStatus;
import com.careerconnect.repository.ApplicationRepo;
import com.careerconnect.repository.CandidateRepo;
import com.careerconnect.repository.InterviewRoomRepository;
import com.careerconnect.repository.RecruiterRepo;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRoomRepository interviewRoomRepository;
    private final ApplicationRepo applicationRepository;
    private final CandidateRepo candidateRepo;
    private final RecruiterRepo recruiterRepo;
//    private final EmailService emailService;
    private final NotificationService notificationService;
    private final AuthenticationHelper authenticationHelper;

    /**
     * Schedule a new interview
     */
    @Transactional
    public ApiResp<InterviewResponse> scheduleInterview(InterviewRequest request) {
        // Validate if the application exists
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException(Application.class, request.getApplicationId()));

        // Get the current recruiter
        Long currentUserId = authenticationHelper.getUserId();
        Recruiter recruiter = recruiterRepo.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, currentUserId));

        // Validate that the recruiter belongs to the company that posted the job
        if (!application.getJob().getCompany().equals(recruiter.getCompany())) {
            throw new RuntimeException("You do not have permission to schedule an interview for this application");
        }

        // Check if the scheduled time is in the future
        if (request.getScheduledTime().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        // Create the interview room
        InterviewRoom interviewRoom = InterviewRoom.builder()
                .applicationId(application.getApplicationId())
                .recruiterId(recruiter.getUserId())
                .candidateId(application.getCandidate().getUserId())
                .scheduledTime(request.getScheduledTime())
                .notes(request.getMessage())
                .status(InterviewStatus.SCHEDULED)
                .startTime(LocalDateTime.now())
                .roomName("Interview Room " + UUID.randomUUID())
                .build();

        interviewRoomRepository.save(interviewRoom);

        // Send email notification to candidate
//        sendInterviewNotification(interviewRoom, true);
        notificationService.createNotification(
                interviewRoom.getCandidateId(),
                "Interview Scheduled",
                "You have a new interview scheduled for " + interviewRoom.getScheduledTime(),
                "interview");
        InterviewResponse response = mapToInterviewResponse(interviewRoom);
        return ApiResp.<InterviewResponse>builder().result(response).build();
    }

    /**
     * Reschedule an existing interview
     */
    @Transactional
    public ApiResp<InterviewResponse> rescheduleInterview(UUID interviewId, InterviewRequest request) {
        Long currentUserId = authenticationHelper.getUserId();
        
        // Find the interview
        InterviewRoom interviewRoom = interviewRoomRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

//         Only the recruiter who created the interview can reschedule it
        if (!interviewRoom.getCandidateId().equals(currentUserId)) {
            throw new RuntimeException("You do not have permission to reschedule this interview");
        }

        // Check if the interview is already completed or cancelled
        if (interviewRoom.getStatus() == InterviewStatus.COMPLETED || 
            interviewRoom.getStatus() == InterviewStatus.CANCELLED) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        // Check if the new scheduled time is in the future
        if (request.getScheduledTime().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        // Update the interview
        interviewRoom.setScheduledTime(request.getScheduledTime());
        interviewRoom.setNotes(request.getMessage());
        interviewRoom.setStatus(InterviewStatus.RESCHEDULED);

        interviewRoomRepository.save(interviewRoom);

        // Send notification about rescheduling
//        sendInterviewNotification(interviewRoom, false);
        notificationService.createNotification(
                interviewRoom.getCandidateId(),
                "Interview Rescheduled",
                "Your interview has been rescheduled to " + interviewRoom.getScheduledTime(),
                "interview");


        InterviewResponse response = mapToInterviewResponse(interviewRoom);
        return ApiResp.<InterviewResponse> builder()
                .result(response)
                .message("Interview rescheduled successfully")
                .build();
    }

    /**
     * Cancel an interview
     */
    @Transactional
    public ApiResp<Void> cancelInterview(UUID interviewId) {
        Long currentUserId = authenticationHelper.getUserId();
        
        // Find the interview
        InterviewRoom interviewRoom = interviewRoomRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        // Check if the user is either the recruiter or the candidate for this interview
        boolean isRecruiter = interviewRoom.getRecruiterId().equals(currentUserId);
        boolean isCandidate = interviewRoom.getCandidateId().equals(currentUserId);

        if (!isRecruiter && !isCandidate) {
            throw new RuntimeException("You do not have permission to cancel this interview");
        }

        // Check if the interview is already completed or cancelled
        if (interviewRoom.getStatus() == InterviewStatus.COMPLETED || 
            interviewRoom.getStatus() == InterviewStatus.CANCELLED) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        // Update the interview status
        interviewRoom.setStatus(InterviewStatus.CANCELLED);

        interviewRoomRepository.save(interviewRoom);

        // Send cancellation notification
        // For simplicity, the cancelledBy is determined by who is making this request
        String cancelledBy = isRecruiter ? "recruiter" : "candidate";
//        sendCancellationNotification(interviewRoom, cancelledBy);
        notificationService.createNotification(
                interviewRoom.getCandidateId(),
                "Interview Cancelled",
                "Your interview has been cancelled by the " + cancelledBy,
                "interview");

        return ApiResp.<Void>builder().build();
    }

    /**
     * Get interview details by ID
     */
    public ApiResp<InterviewResponse> getInterviewById(UUID interviewId) {
        Long currentUserId = authenticationHelper.getUserId();
        
        // Find the interview
        InterviewRoom interviewRoom = interviewRoomRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        // Check if the user is either the recruiter or the candidate for this interview
        boolean isRecruiter = interviewRoom.getRecruiterId().equals(currentUserId);
        boolean isCandidate = interviewRoom.getCandidateId().equals(currentUserId);

        if (!isRecruiter && !isCandidate) {
            throw new RuntimeException("You do not have permission to view this interview");
        }

        InterviewResponse response = mapToInterviewResponse(interviewRoom);
        return ApiResp.<InterviewResponse>builder().result(response).build();
    }

    /**
     * Get all interviews for the current user
     */
    public ApiResp<List<InterviewResponse>> getUserInterviews() {
        Long currentUserId = authenticationHelper.getUserId();
        String userRole = authenticationHelper.getCurrentUser().getRole().getRoleName().name();
        
        List<InterviewRoom> interviews;
        
        // Fetch interviews based on user role
        if ("RECRUITER".equalsIgnoreCase(userRole)) {
            interviews = interviewRoomRepository.findByRecruiterId(currentUserId);
        } else{
            interviews = interviewRoomRepository.findByCandidateId(currentUserId);
        }

        List<InterviewResponse> responseList = interviews.stream()
                .map(this::mapToInterviewResponse)
                .collect(Collectors.toList());

        return ApiResp.<List<InterviewResponse>> builder()
                .result(responseList)
                .message("Interviews fetched successfully")
                .build();
    }

    /**
     * Get interviews for a specific application
     */
    public ApiResp<List<InterviewResponse>> getInterviewsByApplication(Long applicationId) {
        Long currentUserId = authenticationHelper.getUserId();
        String userRole = authenticationHelper.getCurrentUser().getRole().getRoleName().name();
        
        // Verify the application exists
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException(Application.class, applicationId));

        // Verify permissions based on role
        if ("RECRUITER".equalsIgnoreCase(userRole)) {
            Recruiter recruiter = recruiterRepo.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, currentUserId));
            
            if (!application.getJob().getCompany().equals(recruiter.getCompany())) {
                throw new RuntimeException("You do not have permission to view these interviews");
            }
        } else if ("CANDIDATE".equalsIgnoreCase(userRole)) {
            if (!application.getCandidate().getUserId().equals(currentUserId)) {
                throw new RuntimeException("You do not have permission to view these interviews");
            }
        } else {
            throw new RuntimeException("Invalid user role");
        }

        List<InterviewRoom> interviews = interviewRoomRepository.findByApplicationId(applicationId);
        
        List<InterviewResponse> responseList = interviews.stream()
                .map(this::mapToInterviewResponse)
                .collect(Collectors.toList());

        return ApiResp.<List<InterviewResponse>> builder()
                .result(responseList)
                .message("Interviews fetched successfully")
                .build();
    }

    /**
     * Update interview status
     */
    @Transactional
    public ApiResp<InterviewResponse> updateInterviewStatus(UUID interviewId, InterviewStatus status) {
        Long currentUserId = authenticationHelper.getUserId();
        
        // Find the interview
        InterviewRoom interviewRoom = interviewRoomRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        // Check if the user is either the recruiter or the candidate for this interview
        boolean isRecruiter = interviewRoom.getRecruiterId().equals(currentUserId);
        boolean isCandidate = interviewRoom.getCandidateId().equals(currentUserId);

        if (!isRecruiter && !isCandidate) {
            throw new RuntimeException("You do not have permission to update this interview");
        }

        // Update the status
        interviewRoom.setStatus(status);
//        interviewRoom.setUpdatedAt(LocalDateTime.now());

        interviewRoomRepository.save(interviewRoom);

        InterviewResponse response = mapToInterviewResponse(interviewRoom);
        return ApiResp.<InterviewResponse>builder()
                .result(response)
                .message("Interview status updated to " + status)
                .build();

    }

    /**
     * Helper method to map InterviewRoom entity to InterviewResponse DTO
     */
    private InterviewResponse mapToInterviewResponse(InterviewRoom interview) {
        return InterviewResponse.builder()
                .id(interview.getId())
                .applicationId(interview.getApplicationId())
                .recruiterId(interview.getRecruiterId())
                .candidateId(interview.getCandidateId())
                .scheduledTime(interview.getScheduledTime())
                .scheduledTime(interview.getStartTime())
                .status(interview.getStatus().toString())
                .message(interview.getNotes())
                .build();

    }
}