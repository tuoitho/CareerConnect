package com.careerconnect.atest;

import com.careerconnect.config.WebSocketEventListener;
import com.careerconnect.dto.common.MailDTO;
import com.careerconnect.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobAlertConsumer {
    private final MailService mailService;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketEventListener webSocketEventListener;

    @RabbitListener(queues = "${careerconnect.rabbitmq.queue}")
    public void consumeJobAlert(JobAlertMessage message) {
        Long candidateId = message.getCandidateId();
        String notificationMethod = message.getNotificationMethod();

        // Gửi qua WebSocket nếu user online và phương thức cho phép
        if (webSocketEventListener.isUserOnline(candidateId) &&
                ("WEBSOCKET".equals(notificationMethod) || "BOTH".equals(notificationMethod))) {
            messagingTemplate.convertAndSendToUser(
                    candidateId.toString(),
                    "/queue/job-alerts",
                    "New job matching your keyword '" + message.getKeyword() + "': " + message.getJobTitle()
            );
        }

        // Gửi qua email nếu phương thức cho phép
        if ("EMAIL".equals(notificationMethod) || "BOTH".equals(notificationMethod)) {
            MailDTO mailDTO = MailDTO.builder()
                    .from("no-reply@careerconnect.com")
                    .to(message.getEmail())
                    .subject("New Job Alert: " + message.getJobTitle())
                    .text("A new job matching your keyword '" + message.getKeyword() + "' has been posted:\n" +
                            "Title: " + message.getJobTitle() + "\n" +
                            "Location: " + message.getJobLocation() + "\n" +
                            "Description: " + message.getJobDescription() + "\n" +
                            "Apply now at: http://localhost:3000/job/" + message.getJobId())
                    .build();
            mailService.send(mailDTO);
        }
    }
}