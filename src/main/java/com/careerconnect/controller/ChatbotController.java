package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.request.ChatbotMessageRequest;
import com.careerconnect.dto.response.ChatbotMessageResponse;
import com.careerconnect.service.impl.ChatbotService;
import com.careerconnect.util.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoint.PREFIX + "/chatbot")
@Tag(name = "Chatbot", description = "API for AI chatbot functionality")
@SecurityRequirement(name = "bearerAuth")
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final AuthenticationHelper authenticationHelper;

    @PreAuthorize(SecurityEndpoint.CANDIDATE)
    @PostMapping("/message")
    public ResponseEntity<?> sendMessage(@Valid @RequestBody ChatbotMessageRequest request) {
        Long userId = authenticationHelper.getUserId();
        ChatbotMessageResponse response = chatbotService.processMessage(userId, request.getMessage());
        
        return ResponseEntity.ok(ApiResp.builder()
                .message("Chatbot response")
                .result(response)
                .build());
    }
}