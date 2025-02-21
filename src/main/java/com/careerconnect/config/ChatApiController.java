package com.careerconnect.config;

import com.careerconnect.entity.ChatMessage;
import com.careerconnect.util.AuthenticationHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatApiController {


    private final AuthenticationHelper authenticationHelper;
    private final ChatMessageRepo chatMessageRepo;

    public ChatApiController(AuthenticationHelper authenticationHelper, ChatMessageRepo chatMessageRepo) {
        this.authenticationHelper = authenticationHelper;
        this.chatMessageRepo = chatMessageRepo;
    }

    
    @PostMapping("/upload-file")
    public void handleFileUpload(
        @RequestParam("file") MultipartFile file,
        @RequestParam Long recipientId 
    ) {
        // Xử lý upload file và gửi notification qua WebSocket 
    }
}