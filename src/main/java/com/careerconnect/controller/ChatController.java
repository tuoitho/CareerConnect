package com.careerconnect.controller;

import com.careerconnect.config.*;
import com.careerconnect.dto.chat.ChatMessageDTO;
import com.careerconnect.dto.chat.MarkAsDeliveredMessageRequest;
import com.careerconnect.dto.chat.MarkAsReadRequest;
import com.careerconnect.entity.ChatMessage;
import com.careerconnect.enums.MessageStatus;
import com.careerconnect.repository.ChatMessageRepo;
import com.careerconnect.repository.UserRepository;
import com.careerconnect.util.Logger;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Tag(name = "Chat WebSocket", description = "WebSocket API cho chức năng chat thời gian thực")
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepo messageRepository;
    private final UserRepository userRepository;
    private final WebSocketEventListener webSocketEventListener; // Inject để kiểm tra online
    
    @MessageMapping("/chat.send")
    public void sendMessage(
            @Payload ChatMessageDTO messageDTO,
            Principal principal
    ) {
        Logger.log("msgdto",messageDTO);
        Logger.log("nguoi gui pricipal",principal.getName());

        messagingTemplate.convertAndSendToUser(
                messageDTO.getRecipientId().toString(),
                "/queue/messages",
                messageDTO
        );
        saveMessage(messageDTO);


    }

    public void saveMessage(ChatMessageDTO messageDto) {
        ChatMessage message = convertToEntity(messageDto);
        Long tempId = messageDto.getTempId(); // Lấy ID tạm thời từ client
        message.setTimestamp(LocalDateTime.now()); // Đặt timestamp hiện tại
        // Kiểm tra recipient có online không
        if (webSocketEventListener.isUserOnline(messageDto.getRecipientId())) {
            message.setStatus(MessageStatus.DELIVERED);
        }
        message = messageRepository.save(message); // Lưu vào DB
        ChatMessageDTO messageDTO = ChatMessageDTO.builder()
                .id(message.getId())
                .tempId(tempId)
                .senderId(message.getSender().getUserId())
                .recipientId(message.getRecipient().getUserId())
                .content(message.getContent())
                .status(message.getStatus().name())
                .build();
        messagingTemplate.convertAndSend("/topic/chat.messageSaved", messageDTO);
    }

    @MessageMapping("/chat.markAsRead")
    public void markAsRead(@Payload MarkAsReadRequest req) {
        messageRepository.findById(req.getMessageId()).ifPresent(msg -> {
            msg.setStatus(MessageStatus.READ);
            messageRepository.save(msg);
            messagingTemplate.convertAndSend("/topic/chat.markAsRead", req); // Thông báo "Đã đọc"
        });
    }

    @MessageMapping("/chat.markAsDelivered")
    public void markAsDelivered(@Payload MarkAsDeliveredMessageRequest req) {
        messageRepository.findById(req.getMessageId()).ifPresent(msg -> {
            if (msg.getStatus() == MessageStatus.SENT) {
                msg.setStatus(MessageStatus.DELIVERED);
                messageRepository.save(msg);
                messagingTemplate.convertAndSend("/topic/chat.markAsDelivered", req); // Thông báo "Đã nhận"
            }
        });
    }

    @MessageMapping("/chat.typing/{recipientId}")
    public void typing(SimpMessageHeaderAccessor sha, @Payload ChatMessageDTO messageDTO) {

    }
    ChatMessage convertToEntity(ChatMessageDTO messageDTO) {
        ChatMessage message = new ChatMessage();
        message.setSender(userRepository.findById(messageDTO.getSenderId()).get());
        message.setRecipient(userRepository.findById(messageDTO.getRecipientId()).get());
        message.setContent(messageDTO.getContent());
        return message;
    }
}
