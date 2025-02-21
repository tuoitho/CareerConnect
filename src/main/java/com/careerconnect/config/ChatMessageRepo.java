package com.careerconnect.config;

import com.careerconnect.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepo extends JpaRepository<ChatMessage,Long>{
    List<ChatMessage> findAllBySender_userIdAndRecipient_userIdOrSender_userIdAndRecipient_userId(Long uId, Long userId, Long userId1, Long uId1);
}
