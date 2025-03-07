package com.careerconnect.atest;

import com.careerconnect.dto.request.CreateJobRequest;
import com.careerconnect.dto.response.CreateJobResponse;
import com.careerconnect.entity.Company;
import com.careerconnect.entity.Job;
import com.careerconnect.entity.Recruiter;
import com.careerconnect.enums.JobTypeEnum;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService2 {
    private final JobRepo jobRepository;
    private final UserRepository userRepository;
    private final CompanyRepo companyRepo;
    private final JobAlertSubscriptionRepo jobAlertSubscriptionRepo;
    private final RabbitTemplate rabbitTemplate; // Thêm RabbitTemplate

    @Value("${careerconnect.rabbitmq.exchange}")
    private String exchange;

    @Value("${careerconnect.rabbitmq.routingkey}")
    private String routingKey;

    public void createJob(
//            Long userId, CreateJobRequest req
    ) {
//        Recruiter recruiter = (Recruiter) userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, userId));
//        Company company = recruiter.getCompany();
//        if (company == null) {
//           throw  new AppException(ErrorCode.NO_LINKED_COMPANY);
//        }
//
//        Job job = Job.builder()
//                .title(req.getTitle())
//                .description(req.getDescription())
//                .location(req.getLocation())
//                .type(JobTypeEnum.valueOf(req.getType()))
//                .minSalary(req.getMinSalary())
//                .maxSalary(req.getMaxSalary())
//                .created(LocalDateTime.now())
//                .updated(LocalDateTime.now())
//                .deadline(req.getDeadline())
//                .experience(req.getExperience())
//                .category(req.getCategory())
//                .company(company)
//                .build();
//        Job savedJob = jobRepository.save(job);

        // Gửi thông điệp vào RabbitMQ
        notifySubscribers("java12345");

//        return CreateJobResponse.builder()
//                .jobId(savedJob.getJobId())
//                .title(savedJob.getTitle())
//                .description(savedJob.getDescription())
//                .location(savedJob.getLocation())
//                .type(savedJob.getType())
//                .minSalary(savedJob.getMinSalary())
//                .maxSalary(savedJob.getMaxSalary())
//                .created(savedJob.getCreated())
//                .updated(savedJob.getUpdated())
//                .deadline(savedJob.getDeadline())
//                .experience(savedJob.getExperience())
//                .category(savedJob.getCategory())
//                .active(savedJob.isActive())
//                .build();
    }

    private void notifySubscribers(
//            Job job
    String jobContent
    ) {
//        String jobContent = job.getTitle() + " " + job.getDescription() + " " + job.getLocation();
        List<JobAlertSubscription> subscriptions = jobAlertSubscriptionRepo.findAll();
        // Lọc subscription có keyword nằm trong jobContent
        List<JobAlertSubscription> matchingSubscriptions = subscriptions.stream()
                .filter(sub -> jobContent.toLowerCase().contains(sub.getKeyword().toLowerCase()))
                .toList();
        for (JobAlertSubscription subscription : matchingSubscriptions) {
            JobAlertMessage message = JobAlertMessage.builder()
                    .candidateId(subscription.getCandidate().getUserId())
                    .email(subscription.getCandidate().getEmail())
                    .keyword(subscription.getKeyword())
//                    .jobTitle(job.getTitle())
                    .jobTitle("Java Developer")
//                    .jobDescription(job.getDescription())
//                    .jobLocation(job.getLocation())
//                    .jobId(job.getJobId())
                    .notificationMethod(subscription.getNotificationMethod())
                    .build();

            // Đẩy thông điệp vào RabbitMQ
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
        }
    }

    // Các phương thức khác giữ nguyên
}