package com.careerconnect.controller;

import com.careerconnect.service.impl.ChatMessageService;
import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(ApiEndpoint.PREFIX+"/chat")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final AuthenticationHelper authenticationHelper;

    @GetMapping("/history")
    public ResponseEntity<?> getMessages(@RequestParam Long userId2, Principal principal) {
        Long myId=authenticationHelper.getUserId();
        ApiResponse<?> response = ApiResponse.builder().result(chatMessageService.getMessages(myId, userId2)).build();
        return ResponseEntity.ok(response);
    }
}
