package com.careerconnect.repository;

import com.careerconnect.entity.ChatMessage;
import com.careerconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ChatMessageRepo extends JpaRepository<ChatMessage,Long>{
    List<ChatMessage> findAllBySender_userIdAndRecipient_userIdOrSender_userIdAndRecipient_userIdOrderByTimestampAsc(Long senderId, Long recipientId, Long recipientId2, Long senderId2);


    @Query("SELECT DISTINCT cm.recipient FROM ChatMessage cm WHERE cm.sender.userId = :userId " +
            "UNION " +
            "SELECT DISTINCT cm.sender FROM ChatMessage cm WHERE cm.recipient.userId = :userId")
    Set<User> findUsersHavingMessageHistory(@Param("userId") Long userId);
}
