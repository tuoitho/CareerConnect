package com.careerconnect.service.impl;

import com.careerconnect.dto.response.ChatbotMessageResponse;
import com.careerconnect.dto.response.JobRecommendationResponse;
import com.careerconnect.dto.response.SearchJobItemResponse;
import com.careerconnect.entity.Candidate;
import com.careerconnect.entity.Job;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.CandidateRepo;
import com.careerconnect.repository.JobRepo;
import com.careerconnect.util.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        
        // Get candidate info
        Candidate candidate = candidateRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Candidate.class, userId));
        
        if (groqEnabled && groqApiKey != null && !groqApiKey.isEmpty()) {
            try {
                return processWithGroqAI(message, candidate);
            } catch (Exception e) {
                Logger.log("Error processing with Groq AI: " + e.getMessage());
                // Fallback to basic processing if AI fails
                return processWithBasicLogic(message, candidate);
            }
        } else {
            // Fallback to basic processing if AI is not configured
            return processWithBasicLogic(message, candidate);
        }
    }
    

    private ChatbotMessageResponse processWithGroqAI(String message, Candidate candidate) {
        Logger.log("api groq enabled");
        // First, detect if this is a job search request
        boolean isJobRequest = isJobRecommendationRequest(message);
        
        if (isJobRequest) {
            // Extract requirements from the message using AI
            List<String> requirements = extractRequirementsWithAI(message);

            // Get job recommendations based on requirements
            List<JobRecommendationResponse> recommendations = findMatchingJobs(requirements, candidate);

            if (!recommendations.isEmpty()) {
                // Generate response context with AI
                String aiResponse = generateAIResponse(message, true, requirements);

                return ChatbotMessageResponse.builder()
                        .content(aiResponse)
                        .hasRecommendations(true)
                        .jobRecommendations(recommendations)
                        .build();
            } else {
                String aiResponse = generateAIResponse(message, false, requirements);

                return ChatbotMessageResponse.builder()
                        .content(aiResponse)
                        .hasRecommendations(false)
                        .build();
            }
        } else {
            // For general questions, just use AI to generate a response
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
            systemPrompt += " Người dùng đang tìm kiếm việc làm với các yêu cầu: " + String.join(", ", requirements) + 
                    ". Hãy thông báo rằng bạn đã tìm thấy một số công việc phù hợp và sẽ hiển thị chúng bên dưới.";
        }
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.3-70b-versatile");
        
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);
        
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
        
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.5);
        requestBody.put("max_tokens", 500);
        
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
            // Fallback response if AI call fails
            if (hasRecommendations) {
                return "Dựa trên yêu cầu của bạn, tôi đã tìm thấy một số công việc có thể phù hợp:";
            } else {
                return "Tôi không tìm thấy công việc phù hợp với yêu cầu của bạn. Vui lòng thử với các kỹ năng hoặc vị trí khác.";
            }
        }
    }
    

    private List<String> extractRequirementsWithAI(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + groqApiKey);
        
        String systemPrompt = "Bạn là một trợ lý AI chuyên phân tích yêu cầu tìm việc làm. " +
                "Hãy trích xuất các kỹ năng, vị trí, và địa điểm mà người dùng đang tìm kiếm. " +
                "Chỉ trả về danh sách các từ khóa (mỗi từ khóa một dòng), không thêm bất kỳ văn bản nào khác.";
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.3-70b-versatile");
        
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);
        
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", message);
        messages.add(userMsg);
        
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
                
                // Split by newline and clean up
                for (String line : result.split("\\n")) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("-")) {
                        requirements.add(trimmed.replace("-", "").trim());
                    } else if (trimmed.startsWith("-")) {
                        requirements.add(trimmed.substring(1).trim());
                    }
                }
                
                return requirements;
            }
        } catch (Exception e) {
            Logger.log("Error extracting requirements with AI: " + e.getMessage());
        }
        
        // Fallback to basic extraction if AI fails
        return extractJobRequirements(message);
    }
    

    private List<JobRecommendationResponse> findMatchingJobs(List<String> requirements, Candidate candidate) {
        List<JobRecommendationResponse> recommendations = new ArrayList<>();
        
        if (!requirements.isEmpty()) {
            // Use each requirement to find matching jobs
            for (String requirement : requirements) {
                Pageable pageable = PageRequest.of(0, 3);
                Page<Job> jobs = jobRepository.findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        requirement, requirement, pageable);
                
                for (Job job : jobs.getContent()) {
                    // Avoid duplicates
                    if (!recommendations.stream().anyMatch(rec -> rec.getJobId().equals(job.getJobId()))) {
                        recommendations.add(JobRecommendationResponse.builder()
                                .jobId(job.getJobId())
                                .title(job.getTitle())
                                .company(job.getCompany().getName())
                                .location(job.getLocation())
                                .minSalary(Integer.parseInt(job.getMinSalary()))
                                .maxSalary(Integer.parseInt(job.getMaxSalary()))
                                .build());
                    }
                }
            }
        }
        
        // If still no recommendations, get some recent jobs
        if (recommendations.isEmpty()) {
            Pageable pageable = PageRequest.of(0, 5);
            Page<Job> jobs = jobRepository.findAll(pageable);
            
            for (Job job : jobs.getContent()) {
                recommendations.add(JobRecommendationResponse.builder()
                        .jobId(job.getJobId())
                        .title(job.getTitle())
                        .company(job.getCompany().getName())
                        .location(job.getLocation())
                        .minSalary(Integer.parseInt(job.getMinSalary()))
                        .maxSalary(Integer.parseInt(job.getMaxSalary()))
                        .build());
            }
        }
        
        return recommendations;
    }
    
    /**
     * Basic processing without AI integration
     */
    private ChatbotMessageResponse processWithBasicLogic(String message, Candidate candidate) {
        // Detect if user is asking for job recommendations
        if (isJobRecommendationRequest(message)) {
            List<String> requirements = extractJobRequirements(message);
            List<JobRecommendationResponse> recommendations = new ArrayList<>();
            
            // If requirements were extracted, search for jobs matching those criteria
            if (!requirements.isEmpty()) {
                for (String requirement : requirements) {
                    Pageable pageable = PageRequest.of(0, 3); // Get top 3 matches
                    Page<Job> jobs = jobRepository.findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                            requirement, requirement, pageable);
                    
                    for (Job job : jobs.getContent()) {
                        if (!recommendations.stream().anyMatch(rec -> rec.getJobId().equals(job.getJobId()))) {
                            recommendations.add(JobRecommendationResponse.builder()
                                    .jobId(job.getJobId())
                                    .title(job.getTitle())
                                    .company(job.getCompany().getName())
                                    .location(job.getLocation())
                                    .minSalary(Integer.parseInt(job.getMinSalary()))
                                    .maxSalary(Integer.parseInt(job.getMaxSalary()))
                                    .build());
                        }
                    }
                }
            } else {
                // If no specific requirements, suggest jobs based on general categories
                Pageable pageable = PageRequest.of(0, 5); // Get top 5 recent jobs
                Page<Job> jobs = jobRepository.findAll(pageable);
                
                for (Job job : jobs.getContent()) {
                    recommendations.add(JobRecommendationResponse.builder()
                            .jobId(job.getJobId())
                            .title(job.getTitle())
                            .company(job.getCompany().getName())
                            .location(job.getLocation())
                            .minSalary(Integer.parseInt(job.getMinSalary()))
                            .maxSalary(Integer.parseInt(job.getMaxSalary()))
                            .build());
                }
            }
            
            if (recommendations.isEmpty()) {
                return ChatbotMessageResponse.builder()
                        .content("Tôi không tìm thấy công việc phù hợp với yêu cầu của bạn. Vui lòng thử với các kỹ năng hoặc vị trí khác.")
                        .hasRecommendations(false)
                        .build();
            }
            
            return ChatbotMessageResponse.builder()
                    .content("Dựa trên yêu cầu của bạn, tôi tìm thấy một số công việc có thể phù hợp:")
                    .hasRecommendations(true)
                    .jobRecommendations(recommendations)
                    .build();
        }
        
        // Standard Q&A responses
        return ChatbotMessageResponse.builder()
                .content(generateAnswer(message))
                .hasRecommendations(false)
                .build();
    }
    
    /**
     * Checks if a message is requesting job recommendations
     */
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
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", message);
        messages.add(userMsg);

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.1);
        requestBody.put("max_tokens", 10);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GROQ_API_URL, request, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            if (jsonResponse.has("choices") && jsonResponse.get("choices").size() > 0) {
                String result = jsonResponse.get("choices").get(0).get("message").get("content").asText().toLowerCase();
                return result.contains("true");
            }
        } catch (Exception e) {
            Logger.log("Error in AI job request detection: " + e.getMessage());
            throw new AppException(ErrorCode.AI_RESPONSE_ERROR);
        }

        return false;
    }
    
    /**
     * Extracts job skills/requirements from a message
     */
    private List<String> extractJobRequirements(String message) {
        List<String> requirements = new ArrayList<>();
        String lowerMessage = message.toLowerCase();
        
        // Common job skills and roles
        String[] jobKeywords = {
            "java", "python", "javascript", "react", "angular", "vue", "node", "php", 
            "developer", "programmer", "engineer", "manager", "designer", "data", "ai", 
            "machine learning", "full stack", "backend", "frontend", "devops", "qa", "tester", 
            "product", "project", "ui", "ux", "mobile", "web", "cloud", "security", "network"
        };
        
        // Extract skills mentioned in the message
        for (String keyword : jobKeywords) {
            if (lowerMessage.contains(keyword.toLowerCase())) {
                requirements.add(keyword);
            }
        }
        
        // Extract location information
        String[] locations = {
            "hà nội", "ho chi minh", "hồ chí minh", "đà nẵng", "da nang", 
            "hải phòng", "hai phong", "cần thơ", "can tho"
        };
        
        for (String location : locations) {
            if (lowerMessage.contains(location)) {
                requirements.add(location);
                break;
            }
        }
        
        // If no specific skills were found, look for more general categories
        if (requirements.isEmpty()) {
            // Check for roles
            Pattern pattern = Pattern.compile("\\b(developer|programmer|engineer|manager|designer)\\b");
            Matcher matcher = pattern.matcher(lowerMessage);
            if (matcher.find()) {
                requirements.add(matcher.group(1));
            }
        }
        
        return requirements;
    }
    

    /**
     * Generates a text response to a user's question
     */
    private String generateAnswer(String message) {
        String lowerMessage = message.toLowerCase();
        
        // Career advice questions
        if (lowerMessage.contains("sự nghiệp") || lowerMessage.contains("ngành nghề")) {
            return "Để phát triển sự nghiệp, bạn nên xác định rõ mục tiêu nghề nghiệp, học hỏi liên tục, và xây dựng mạng lưới quan hệ trong ngành. Bạn có thể sử dụng CareerConnect để tìm kiếm cơ hội việc làm phù hợp và kết nối với các nhà tuyển dụng.";
        }
        
        // Resume/CV advice
        if (lowerMessage.contains("cv") || lowerMessage.contains("resume") || lowerMessage.contains("hồ sơ")) {
            return "Một CV hiệu quả nên ngắn gọn, rõ ràng và tập trung vào thành tích. Hãy đảm bảo CV của bạn có thông tin liên hệ đầy đủ, kinh nghiệm làm việc liên quan, và kỹ năng phù hợp với vị trí ứng tuyển. Bạn có thể tạo và quản lý CV trong phần Hồ sơ ứng viên của CareerConnect.";
        }
        
        // Interview preparation
        if (lowerMessage.contains("phỏng vấn") || lowerMessage.contains("interview")) {
            return "Để chuẩn bị cho phỏng vấn, hãy nghiên cứu kỹ về công ty và vị trí ứng tuyển, chuẩn bị câu trả lời cho các câu hỏi phổ biến, và chuẩn bị câu hỏi để hỏi nhà tuyển dụng. Bạn nên đến sớm, ăn mặc phù hợp và thể hiện sự tự tin.";
        }
        
        // Salary negotiation
        if (lowerMessage.contains("lương") || lowerMessage.contains("đãi ngộ") || lowerMessage.contains("phúc lợi")) {
            return "Khi thương lượng lương, hãy nghiên cứu mức lương thị trường cho vị trí tương tự, nhấn mạnh giá trị bạn mang lại, và xem xét toàn bộ gói đãi ngộ (không chỉ lương cơ bản). Dựa trên kinh nghiệm và kỹ năng của bạn, hãy đưa ra một khoảng lương phù hợp thay vì một con số cụ thể.";
        }
        
        // Job application advice
        if (lowerMessage.contains("ứng tuyển") || lowerMessage.contains("nộp đơn") || lowerMessage.contains("apply")) {
            return "Khi ứng tuyển việc làm, hãy điều chỉnh CV và thư xin việc cho phù hợp với từng vị trí, nhấn mạnh những kỹ năng và kinh nghiệm liên quan đến công việc. Theo dõi đơn ứng tuyển của bạn và gửi email cảm ơn sau khi phỏng vấn.";
        }
        
        // Job search strategy
        if (lowerMessage.contains("chiến lược") || lowerMessage.contains("cách tìm việc")) {
            return "Một chiến lược tìm việc hiệu quả bao gồm việc xác định mục tiêu nghề nghiệp rõ ràng, tạo hồ sơ chuyên nghiệp, mở rộng mạng lưới quan hệ, và tìm kiếm có chọn lọc. Hãy sử dụng CareerConnect để tìm kiếm việc làm phù hợp với kỹ năng và mong muốn của bạn.";
        }
        
        // Default response for other questions
        return "Xin chào! Tôi là trợ lý AI của CareerConnect. Tôi có thể giúp bạn tìm việc làm phù hợp, cung cấp lời khuyên về sự nghiệp, và trả lời các câu hỏi về quá trình ứng tuyển. Bạn có thể hỏi tôi về 'tìm việc làm', 'chuẩn bị CV', 'kỹ năng phỏng vấn', hoặc nói về kỹ năng của bạn để nhận đề xuất công việc phù hợp.";
    }

    public Flux<String> streamResponse(Long userId, String message) {
        Logger.log("Streaming response for user " + userId + ": " + message);
        
        // Get candidate info
        Candidate candidate = candidateRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Candidate.class, userId));
        
        if (!groqEnabled || groqApiKey == null || groqApiKey.isEmpty()) {
            // If AI is not configured, return a non-streamed response
            Logger.log("not ai enabled");
            String response = generateAnswer(message);
            return Flux.just(response);
        }
        
        // First, determine if this is a job query
        boolean isJobRequest = isJobRecommendationRequest(message);
        List<String> requirements = new ArrayList<>();
        
        if (isJobRequest) {
            // For job requests, extract requirements first to create better prompts
            try {
                requirements = extractRequirementsWithAI(message);
            } catch (Exception e) {
                requirements = extractJobRequirements(message);
            }
        }
        
        // Create the request for streaming
        return streamAIResponse(message, isJobRequest, requirements, candidate);
    }
    

    private Flux<String> streamAIResponse(String userMessage, boolean isJobRequest, List<String> requirements,Candidate candidate ) {
        Logger.log("dang stream");
        String systemPrompt = "Bạn là trợ lý AI của CareerConnect, một nền tảng tìm kiếm việc làm. " +
                "Hãy trả lời ngắn gọn, hữu ích và chuyên nghiệp bằng tiếng Việt. " +
                "Không được đưa ra thông tin sai lệch. Nếu bạn không biết câu trả lời, hãy nói rằng bạn không có thông tin.";
        
        if (isJobRequest && !requirements.isEmpty()) {
            systemPrompt += " Người dùng đang tìm kiếm việc làm với các yêu cầu: " + String.join(", ", requirements) + 
                    ". Sau khi trả lời, hãy cho họ biết rằng bạn đang tìm kiếm các công việc phù hợp và sẽ hiển thị chúng sau khi trò chuyện.";
        }
        
        // Build the request body
        Map<String, Object> requestBody = buildStreamRequestBody(systemPrompt, userMessage);
        
        // Use WebClient to call Groq API with streaming enabled
        return webClient.post()
                .uri(GROQ_API_URL)
                .header("Authorization", "Bearer " + groqApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(chunk -> Logger.log("Raw chunk: " + chunk)) // Log raw data
                .flatMap(this::processStreamChunk);
    }
    
    /**
     * Build the request body for streaming API call
     */
    private Map<String, Object> buildStreamRequestBody(String systemPrompt, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        
        // Add system message
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);
        
        // Add user message
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
        
        // Build the complete request
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.3-70b-specdec");
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.5);
        requestBody.put("max_tokens", 500);
        requestBody.put("stream", true);  // Enable streaming
        
        return requestBody;
    }
    
    /**
     * Process each chunk from the streaming response
     */
    private Flux<String> processStreamChunk(String chunk) {
        try {
            // Skip the [DONE] chunk
            if (chunk.trim().equals("[DONE]")) {
                return Flux.empty(); // No content to emit, just complete the stream
            }

            // Parse the chunk as JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(chunk);

            // Get the first choice’s delta object
            JsonNode deltaNode = jsonNode.path("choices").path(0).path("delta");

            // Check if there’s content in the delta
            if (deltaNode.has("content")) {
                String content = deltaNode.get("content").asText();
                return Flux.just(content);
            }

            // If no content (e.g., finish_reason chunk), return empty
            return Flux.empty();
        } catch (Exception e) {
            Logger.log("Error processing chunk: " + chunk + ", error: " + e.getMessage());
            return Flux.error(new RuntimeException("Failed to process stream chunk", e));
        }
    }
}