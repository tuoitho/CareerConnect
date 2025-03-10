package com.careerconnect.repository;

import com.careerconnect.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Notification> findByUser_UserIdAndIsReadFalse(Long userId);

    List<Notification> findByUser_userIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findUnreadByUser_UserId(Long userId);

}