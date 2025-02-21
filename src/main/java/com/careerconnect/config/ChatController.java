package com.careerconnect.config;

import com.careerconnect.entity.ChatMessage;
import com.careerconnect.enums.MessageStatus;
import com.careerconnect.repository.UserRepository;
import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepo messageRepository;
    private final UserRepository userRepository;

    @MessageMapping("/chat.send")
    public void sendMessage(SimpMessageHeaderAccessor sha,
            @Payload ChatMessageDTO messageDTO,
            Principal principal
    ) {
        Logger.log("msgdto",messageDTO);
        Logger.log("principal",sha.getUser());

        messagingTemplate.convertAndSendToUser(
//                messageDTO.getRecipientId().toString(),
                "a",

                "/queue/messages",
                messageDTO.getContent()
        );

    }

    @MessageMapping("/chat.markAsRead")
    public void markAsRead(@Payload Long messageId) {
        messageRepository.findById(messageId).ifPresent(msg -> {
            msg.setStatus(MessageStatus.READ);
            messageRepository.save(msg);
        });
    }

    @MessageMapping("/chat.typing/{recipientId}")
    public void typing(SimpMessageHeaderAccessor sha, @Payload ChatMessageDTO messageDTO) {
//        Logger.log("Principal: " + sha.getUser().getName());
//        Logger.log("Typing: " + messageDTO);
//        messagingTemplate.convertAndSendToUser(
//                messageDTO.getRecipientId().toString(),
//                "/queue/typing",
//                messageDTO.getSenderId()
//        );
    }
    ChatMessage convertToEntity(ChatMessageDTO messageDTO) {
        ChatMessage message = new ChatMessage();
        message.setSender(userRepository.findById(messageDTO.getSenderId()).get());
        message.setRecipient(userRepository.findById(messageDTO.getRecipientId()).get());
        message.setContent(messageDTO.getContent());
        return message;
    }
}
 