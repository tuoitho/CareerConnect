package com.careerconnect.controller;

import com.careerconnect.repository.ChatMessageRepo;
import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.service.impl.ChatService;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatApiController {


    private final AuthenticationHelper authenticationHelper;
    private final ChatMessageRepo chatMessageRepo;
    private final ChatService chatService;

    @GetMapping("/recruiter-contacts")
    public ResponseEntity<?> getRecruiterContacts() {
        Long userId = authenticationHelper.getUserId();
        ApiResponse<?> response = ApiResponse.builder().result(chatService.getRecruitersForCandidate(userId)).build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/candidate-contacts")
    public ResponseEntity<?> getUsersWithMessageHistory() {
        Long userId = authenticationHelper.getUserId();
        ApiResponse<?> response = ApiResponse.builder().result(chatService.getCandidatesForRecruiter(userId)).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-file")
    public void handleFileUpload(
        @RequestParam("file") MultipartFile file,
        @RequestParam Long recipientId 
    ) {
        // Xử lý upload file và gửi notification qua WebSocket 
    }
}