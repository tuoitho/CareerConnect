package com.careerconnect.controller;

import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.response.NotificationResponse;
import com.careerconnect.service.impl.NotificationService;
import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.dto.common.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoint.PREFIX + "/notifications")
@RequiredArgsConstructor
@PreAuthorize(SecurityEndpoint.BOTH)
public class NotificationController {

    private final NotificationService notificationService;

    // Lấy danh sách thông báo
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<NotificationResponse>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        PaginatedResponse<NotificationResponse> result = notificationService.getNotifications(page, size);
        ApiResponse<PaginatedResponse<NotificationResponse>> response = ApiResponse.<PaginatedResponse<NotificationResponse>>builder()
                .message("Notifications retrieved successfully")
                .result(result)
                .build();
        return ResponseEntity.ok(response);
    }

    // Đánh dấu một thông báo là đã đọc
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable Long notificationId) {
        NotificationResponse result = notificationService.markAsRead(notificationId);
        ApiResponse<NotificationResponse> response = ApiResponse.<NotificationResponse>builder()
                .message("Notification marked as read")
                .result(result)
                .build();
        return ResponseEntity.ok(response);
    }

    // Đánh dấu tất cả thông báo là đã đọc
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        notificationService.markAllAsRead();
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .message("All notifications marked as read")
                .build();
        return ResponseEntity.ok(response);
    }

    // Xóa một thông báo
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .message("Notification deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}