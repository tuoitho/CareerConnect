package com.careerconnect.event;

import com.careerconnect.entity.Application;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobApplicationProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${careerconnect.rabbitmq.direct.exchange}")
    private String exchange;

    @Value("${careerconnect.rabbitmq.application.routingkey}")
    private String routingKey;

    public void notifyJobApplication(Application message) {
        JobApplicationMessage jobApplicationMessage = JobApplicationMessage.builder()
                .jobId(message.getJob().getJobId())
                .jobTitle(message.getJob().getTitle())
                .candidateId(message.getCandidate().getUserId())
                .build();
        rabbitTemplate.convertAndSend(exchange, routingKey, jobApplicationMessage);
    }
}