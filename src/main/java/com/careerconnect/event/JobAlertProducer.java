package com.careerconnect.event;

import com.careerconnect.dto.other.JobAlertMessage;
import com.careerconnect.entity.Job;
import com.careerconnect.entity.JobAlertSubscription;
import com.careerconnect.repository.JobAlertSubscriptionRepo;
import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobAlertProducer {
    private final JobAlertSubscriptionRepo jobAlertSubscriptionRepo;
    private final RabbitTemplate rabbitTemplate; // Thêm RabbitTemplate

    @Value("${careerconnect.rabbitmq.exchange}")
    private String exchange;

    @Value("${careerconnect.rabbitmq.routingkey}")
    private String routingKey;

    public void notifySubscribers(Job job) {
        String jobContent = job.getTitle() + " " + job.getDescription();
        List<JobAlertSubscription> subscriptions = jobAlertSubscriptionRepo.findMatchingSubscriptions(jobContent);
        for (JobAlertSubscription subscription : subscriptions) {
            JobAlertMessage message = JobAlertMessage.builder()
                    .candidateId(subscription.getCandidate().getUserId())
                    .email(subscription.getCandidate().getEmail())
                    .keyword(subscription.getKeyword())
                    .jobTitle(job.getTitle())
                    .jobDescription(job.getDescription())
                    .jobLocation(job.getLocation())
                    .jobId(job.getJobId())
                    .notificationMethod(subscription.getNotificationMethod())
                    .build();

            // Đẩy thông điệp vào RabbitMQ
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
        }
    }
}
