package com.careerconnect.service.impl;

import com.careerconnect.dto.response.NotificationResponse;
import com.careerconnect.dto.common.PaginatedResponse;
import com.careerconnect.entity.Notification;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.NotificationRepository;
import com.careerconnect.repository.UserRepository;
import com.careerconnect.service.PaginationService;
import com.careerconnect.util.AuthenticationHelper;
import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PaginationService paginationService;
    private final AuthenticationHelper authenticationHelper;
    private final UserRepository userRepository;

    // Lấy danh sách thông báo phân trang
    public PaginatedResponse<NotificationResponse> getNotifications(int page, int size) {
        Long userId = authenticationHelper.getUserId();
        Logger.log("getNotifications userId = " + userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> notifications = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId, pageable);

        return paginationService.paginate(notifications, notification -> NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .time(notification.getCreatedAt().toString())
                .isRead(notification.isRead())
                .type(notification.getType())
                .build());
    }

    // Đánh dấu một thông báo là đã đọc
    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        Long userId = authenticationHelper.getUserId();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(Notification.class, notificationId));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You do not have permission to modify this notification");
        }

        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);

        return NotificationResponse.builder()
                .id(updatedNotification.getId())
                .title(updatedNotification.getTitle())
                .message(updatedNotification.getMessage())
                .time(updatedNotification.getCreatedAt().toString())
                .isRead(updatedNotification.isRead())
                .type(updatedNotification.getType())
                .build();
    }

    // Đánh dấu tất cả thông báo là đã đọc
    @Transactional
    public void markAllAsRead() {
        Long userId = authenticationHelper.getUserId();
        List<Notification> unreadNotifications = notificationRepository.findByUser_UserIdAndIsReadFalse(userId);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    // Xóa một thông báo
    @Transactional
    public void deleteNotification(Long notificationId) {
        Long userId = authenticationHelper.getUserId();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(Notification.class, notificationId));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You do not have permission to delete this notification");
        }

        notificationRepository.delete(notification);
    }

    // Tạo thông báo mới (gọi từ các service khác khi cần)
    @Transactional
    public void createNotification(Long userId, String title, String message, String type) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .type(type)
                .user(userRepository.findById(userId).orElseThrow()) // Lấy user từ AuthenticationHelper
                .build();
        notificationRepository.save(notification);
    }
}