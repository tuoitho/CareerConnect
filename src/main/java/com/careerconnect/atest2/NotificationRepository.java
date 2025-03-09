package com.careerconnect.atest2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUser_UserId(Long userId, Pageable pageable);

    List<Notification> findByUser_UserIdAndIsReadFalse(Long userId);
}