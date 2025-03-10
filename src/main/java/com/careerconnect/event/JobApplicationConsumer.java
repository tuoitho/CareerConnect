package com.careerconnect.event;

import com.careerconnect.entity.Candidate;
import com.careerconnect.entity.Notification;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.CandidateRepo;
import com.careerconnect.repository.NotificationRepository;
import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobApplicationConsumer {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final CandidateRepo candidateRepo;

    @RabbitListener(queues = "${careerconnect.rabbitmq.application.queue}")
    public void consumeJobApplication(JobApplicationMessage message) {
        Logger.log("Received job application notification: " + message);
        Candidate candidate = candidateRepo.findById(message.getCandidateId()).orElseThrow( () -> new ResourceNotFoundException(Candidate.class, message.getCandidateId()));
        // Create notification for employer
        Notification no = Notification.builder()
                .user(candidate)
                .title("Ứng tuyển thành công")
                .type("application")
                .message("Bạn đã ứng tuyển vào công việc " + message.getJobTitle())
                .build();
        
        notificationRepository.save(no);
        
        // Notify employer through WebSocket if they're online
//        messagingTemplate.convertAndSendToUser(
//                message.getEmployerId().toString(),
//                "/queue/notifications",
//                employerNotification
//        );
//
        Logger.log("Saved and sent notification to: " + message.getCandidateId());
    }
}