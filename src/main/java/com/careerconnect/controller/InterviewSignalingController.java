package com.careerconnect.controller;

import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.request.SignalRequest;
import com.careerconnect.dto.response.SignalResponse;
import com.careerconnect.model.interview.InterviewRoom;
import com.careerconnect.model.interview.InterviewStatus;
import com.careerconnect.repository.InterviewRoomRepository;
import com.careerconnect.util.AuthenticationHelper;
import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class InterviewSignalingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final AuthenticationHelper authenticationHelper;
    private final InterviewRoomRepository interviewRoomRepository;

    /**
     * Handle WebRTC signaling messages (offer, answer, ice candidates)
     */
    @MessageMapping("/interview.signal")
    public void processSignal(@Payload SignalRequest signal, Principal principal) {
        Long currentUserId = Long.parseLong(principal.getName());
        UUID interviewId = signal.getInterviewId();
        
        // Find the interview
        InterviewRoom interview = interviewRoomRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));
        
        // Verify user is part of this interview
        boolean isRecruiter = interview.getRecruiterId().equals(currentUserId);
        boolean isCandidate = interview.getCandidateId().equals(currentUserId);
        
//        if (!isRecruiter && !isCandidate) {
//            throw new RuntimeException("Not authorized to join this interview");
//        }
        
        // Determine recipient based on who sent the message
        Long recipientId = isRecruiter ? interview.getCandidateId() : interview.getRecruiterId();
        
        // Update status to STARTED if needed
        if (interview.getStatus() == InterviewStatus.SCHEDULED || interview.getStatus() ==InterviewStatus.RESCHEDULED) {
            interview.setStatus(InterviewStatus.STARTED);
            interviewRoomRepository.save(interview);
        }
        
        // Forward the signal to the recipient
        SignalResponse responseSignal = SignalResponse.builder()
                .interviewId(signal.getInterviewId())
                .type(signal.getType())
                .senderId(currentUserId)
                .data(signal.getData())
                .build();
        
        messagingTemplate.convertAndSendToUser(
                recipientId.toString(),
                "/queue/interview/signal",
                responseSignal
        );
    }
    
    /**
     * Handle chat messages during interviews
     */
    @MessageMapping("/interview.message")
    public void processMessage(@Payload SignalRequest message,Principal principal) {
        Long currentUserId = Long.parseLong(principal.getName());
        UUID interviewId = message.getInterviewId();
        
        // Find the interview
        InterviewRoom interview = interviewRoomRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));
        
        // Verify user is part of this interview
        boolean isRecruiter = interview.getRecruiterId().equals(currentUserId);
        boolean isCandidate = interview.getCandidateId().equals(currentUserId);
        Logger.log("User ID: " + currentUserId);
        Logger.log("Recruiter ID: " + interview.getRecruiterId());
        Logger.log("Candidate ID: " + interview.getCandidateId());
        Logger.log("Is Recruiter: " + isRecruiter);
        if (!isRecruiter && !isCandidate) {
            throw new RuntimeException("Not authorized to send messages in this interview");
        }
        
        // Determine recipient based on who sent the message
        Long recipientId = isRecruiter ? interview.getCandidateId() : interview.getRecruiterId();
        
        // Forward the message to the recipient
        SignalResponse responseMessage = SignalResponse.builder()
                .interviewId(message.getInterviewId())
                .type("chat")
                .senderId(currentUserId)
                .senderName(isRecruiter ? interview.getRecruiterId().toString() : interview.getCandidateId().toString())
                .data(message.getData())
                .build();
        
        messagingTemplate.convertAndSendToUser(
                recipientId.toString(),
                "/queue/interview/message",
                responseMessage
        );
    }
}