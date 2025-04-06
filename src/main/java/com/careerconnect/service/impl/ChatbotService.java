package com.careerconnect.service.impl;

import com.careerconnect.dto.response.ChatbotMessageResponse;
import com.careerconnect.dto.response.JobRecommendationResponse;
import com.careerconnect.dto.response.SearchJobItemResponse;
import com.careerconnect.entity.Candidate;
import com.careerconnect.entity.Job;
import com.careerconnect.enums.ExpEnum;
import com.careerconnect.enums.JobTypeEnum;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.CandidateRepo;
import com.careerconnect.repository.JobRepo;
import com.careerconnect.util.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotService {
    private final JobRepo jobRepository;
    private final CandidateRepo candidateRepo;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient;

    @Value("${ai.groq.api-key:}")
    private String groqApiKey;
    
    @Value("${ai.groq.enabled:false}")
    private boolean groqEnabled;

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";


    public ChatbotMessageResponse processMessage(Long userId, String message) {
        Logger.log("Processing message from user " + userId + ": " + message);

        Candidate candidate = candidateRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Candidate.class, userId));

        return processWithGroqAI(message, candidate);
    }

    private ChatbotMessageResponse processWithGroqAI(String message, Candidate candidate) {
        boolean isJobRequest = isJobRecommendationRequest(message);

        if (isJobRequest) {
            Logger.log("co yeu cau");
            List<String> requirements = extractRequirementsWithAI(message);
            List<JobRecommendationResponse> recommendations = findMatchingJobs(requirements, candidate);
            String aiResponse = generateAIResponse(message, isJobRequest, requirements);
            return ChatbotMessageResponse.builder()
                    .content(aiResponse)
                    .hasRecommendations(isJobRequest)
                    .jobRecommendations(recommendations)
                    .build();

        } else {
            String aiResponse = generateAIResponse(message, false, null);
            return ChatbotMessageResponse.builder()
                    .content(aiResponse)
                    .hasRecommendations(false)
                    .build();
        }
    }

    private String generateAIResponse(String userMessage, boolean hasRecommendations, List<String> requirements) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + groqApiKey);

        String systemPrompt = "Bạn là trợ lý AI của CareerConnect, một nền tảng tìm kiếm việc làm. " +
                "Hãy trả lời ngắn gọn, hữu ích và chuyên nghiệp bằng tiếng Việt. " +
                "Không được đưa ra thông tin sai lệch. Nếu bạn không biết câu trả lời, hãy nói rằng bạn không có thông tin.";

        if (hasRecommendations) {
            systemPrompt += " Người dùng đang tìm kiếm việc làm với các yêu cầu: '" + userMessage+
                    "'. Hãy thông báo như sau: 'Tôi đã tìm thấy một số công việc phù hợp, sau đây là danh sách các công việc liên quan đến ... Thay dấu '...' bằng các từ khóa mà bạn đã trích xuất từ yêu cầu của người dùng. Sau đó không nói gì thêm.";
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.3-70b-versatile");
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
//        messages.add(Map.of("role", "user", "content", userMessage));
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.4);
        requestBody.put("max_tokens", 100);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GROQ_API_URL, request, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            if (jsonResponse.has("choices") && jsonResponse.get("choices").size() > 0) {
                return jsonResponse.get("choices").get(0).get("message").get("content").asText();
            } else {
                throw new AppException(ErrorCode.AI_RESPONSE_ERROR);
            }
        } catch (Exception e) {
            Logger.log("Error generating AI response: " + e.getMessage());
            return hasRecommendations
                    ? "Dựa trên yêu cầu của bạn, tôi đã tìm thấy một số công việc có thể phù hợp:"
                    : "Tôi không tìm thấy thông tin phù hợp. Vui lòng thử lại.";
        }
    }

    private List<String> extractRequirementsWithAI(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + groqApiKey);

        String systemPrompt = "Bạn là một trợ lý AI chuyên phân tích yêu cầu tìm việc làm. " +
                "Hãy trích xuất các thông tin sau từ tin nhắn của người dùng: " +
                "tên công việc (title), kỹ năng, vị trí (location), loại công việc (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, TEMPORARY, VOLUNTEER, FREELANCE), " +
                "mức kinh nghiệm (ENTRY_LEVEL, MID_LEVEL, SENIOR_LEVEL, EXECUTIVE, NO_EXPERIENCE, INTERN, FRESHER), " +
                "khoảng lương tối thiểu và tối đa (minSalary, maxSalary), danh mục (category), khu vực (area). " +
                "Chỉ trả về danh sách các từ khóa (mỗi từ khóa một dòng), không thêm bất kỳ văn bản nào khác.";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.3-70b-versatile");
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", message));
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.1);
        requestBody.put("max_tokens", 200);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GROQ_API_URL, request, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            if (jsonResponse.has("choices") && jsonResponse.get("choices").size() > 0) {
                String result = jsonResponse.get("choices").get(0).get("message").get("content").asText();
                List<String> requirements = new ArrayList<>();
                for (String line : result.split("\\n")) {
                    String trimmed = line.trim();
                    Logger.log(trimmed);
                    if (!trimmed.isEmpty() && !trimmed.startsWith("-")) {
                        requirements.add(trimmed);
                    } else if (trimmed.startsWith("-")) {
                        requirements.add(trimmed.substring(1).trim());
                    }
                }
                return requirements;
            }
        } catch (Exception e) {
            Logger.log("Error extracting requirements with AI: " + e.getMessage());
        }
        throw new AppException(ErrorCode.AI_RESPONSE_ERROR);
    }

    private List<JobRecommendationResponse> findMatchingJobs(List<String> requirements, Candidate candidate) {
        Logger.log("finding jobs");
        if (requirements.isEmpty()) {
            return getDefaultRecommendations();
        }

        PageRequest pageRequest = PageRequest.of(0, 3); // Limit to 3 results per query
        Set<Job> uniqueJobs = new LinkedHashSet<>(); // Preserve order, avoid duplicates

        //uu tien tim theo title
        for (String requirement : requirements) {
            Page<Job> jobs = jobRepository.findAllByTitleContaining(requirement.trim().toLowerCase(), pageRequest);
            uniqueJobs.addAll(jobs.getContent());
            if (uniqueJobs.size() >= 3) break; // Stop at 3 unique jobs
        }
        for (String requirement : requirements) {
            Logger.log("Requirement: " + requirement);
            Page<Job> jobs = jobRepository.findAllByCriteria(requirement.trim().toLowerCase(), pageRequest);
            uniqueJobs.addAll(jobs.getContent());
            if (uniqueJobs.size() >= 3) break; // Stop at 3 unique jobs
        }

        return uniqueJobs.stream()
                .limit(3) // Ensure max of 3
                .map(job -> JobRecommendationResponse.builder()
                        .jobId(job.getJobId())
                        .title(job.getTitle())
                        .company(job.getCompany() != null ? job.getCompany().getName() : "N/A")
                        .location(job.getLocation())
                        .minSalary(Integer.parseInt(job.getMinSalary()))
                        .maxSalary(Integer.parseInt(job.getMaxSalary()))
                        .build())
                .collect(Collectors.toList());
    }




    private List<JobRecommendationResponse> getDefaultRecommendations() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Job> jobs = jobRepository.findAllByActiveTrue(pageable);
        return jobs.getContent().stream()
                .map(job -> JobRecommendationResponse.builder()
                        .jobId(job.getJobId())
                        .title(job.getTitle())
                        .company(job.getCompany() != null ? job.getCompany().getName() : "N/A")
                        .location(job.getLocation())
                        .minSalary(Integer.parseInt(job.getMinSalary()))
                        .maxSalary(Integer.parseInt(job.getMaxSalary()))
                        .build())
                .collect(Collectors.toList());
    }

    private boolean isJobRecommendationRequest(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + groqApiKey);

        String systemPrompt = "Bạn là một trợ lý AI phân tích yêu cầu của người dùng. " +
                "Nhiệm vụ của bạn là xác định xem người dùng có đang tìm kiếm công việc hay không. " +
                "Chỉ trả về 'true' nếu người dùng đang tìm kiếm việc làm hoặc đề cập đến kỹ năng/ngành nghề " +
                "với ý định tìm kiếm việc làm. Ngược lại, trả về 'false'.";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.3-70b-versatile");
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", message));
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.1);
        requestBody.put("max_tokens", 5);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GROQ_API_URL, request, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            String res=jsonResponse.get("choices").get(0).get("message").get("content").asText().toLowerCase();
            Logger.log(res);
            return res.contains("true");
        } catch (Exception e) {
            Logger.log("Error in AI job request detection: " + e.getMessage());
            return false;
        }
    }

}