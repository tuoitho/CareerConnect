package com.careerconnect.config;

import com.careerconnect.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageRepo chatMessageRepo;

    public List<ChatMessageResponse> getMessages(Long senderId, Long recipientId) {
        List<ChatMessage> messages = chatMessageRepo.findAllBySender_userIdAndRecipient_userIdOrSender_userIdAndRecipient_userIdOrderByTimestampAsc(senderId, recipientId, recipientId, senderId);
        return messages.stream().map(
                message -> ChatMessageResponse.builder()
                        .id(message.getId())
                        .senderId(message.getSender().getUserId())
                        .recipientId(message.getRecipient().getUserId())
                        .content(message.getContent())
                        .timestamp(message.getTimestamp())
                        .status(message.getStatus()!=null?message.getStatus().name():null)
                        .build()
        ).collect(Collectors.toList());
    }
}
