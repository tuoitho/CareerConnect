package com.careerconnect.controller;

import com.careerconnect.repository.ChatMessageRepo;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.service.impl.ChatService;
import com.careerconnect.util.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat API", description = "REST API để quản lý lịch sử chat và danh sách liên hệ")
@SecurityRequirement(name = "bearerAuth")
public class ChatApiController {

    private final AuthenticationHelper authenticationHelper;
    private final ChatService chatService;
    
    @Operation(summary = "Lấy danh sách nhà tuyển dụng đã chat", description = "API lấy danh sách nhà tuyển dụng đã nhắn tin với ứng viên hiện tại")
    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/recruiter-contacts")
    public ResponseEntity<?> getRecruiterContacts() {
        Long userId = authenticationHelper.getUserId();
        ApiResp<?> response = ApiResp.builder().result(chatService.getRecruitersForCandidate(userId)).build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Lấy danh sách ứng viên đã chat", description = "API lấy danh sách ứng viên đã nhắn tin với nhà tuyển dụng hiện tại")
    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/candidate-contacts")
    public ResponseEntity<?> getUsersWithMessageHistory() {
        Long userId = authenticationHelper.getUserId();
        ApiResp<?> response = ApiResp.builder().result(chatService.getCandidatesForRecruiter(userId)).build();
        return ResponseEntity.ok(response);
    }


}