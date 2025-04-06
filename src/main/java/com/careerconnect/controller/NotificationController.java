package com.careerconnect.controller;

import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.response.NotificationResponse;
import com.careerconnect.service.impl.NotificationService;
import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.common.PaginatedResponse;
import com.careerconnect.util.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(ApiEndpoint.PREFIX + "/notifications")
@RequiredArgsConstructor
@PreAuthorize(SecurityEndpoint.BOTH)
@Tag(name = "Notifications", description = "API quản lý thông báo cho người dùng")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Lấy danh sách thông báo", description = "API lấy danh sách thông báo của người dùng hiện tại")
    @GetMapping
    public ResponseEntity<ApiResp<PaginatedResponse<NotificationResponse>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Logger.log("get notifications");
        PaginatedResponse<NotificationResponse> result = notificationService.getNotifications(page, size);
        ApiResp<PaginatedResponse<NotificationResponse>> response = ApiResp.<PaginatedResponse<NotificationResponse>>builder()
                .message("Notifications retrieved successfully")
                .result(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Đánh dấu thông báo đã đọc", description = "API đánh dấu một thông báo cụ thể là đã đọc")
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResp<NotificationResponse>> markAsRead(@PathVariable Long notificationId) {
        NotificationResponse result = notificationService.markAsRead(notificationId);
        ApiResp<NotificationResponse> response = ApiResp.<NotificationResponse>builder()
                .message("Notification marked as read")
                .result(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Đánh dấu tất cả thông báo đã đọc", description = "API đánh dấu tất cả thông báo của người dùng là đã đọc")
    @PutMapping("/read-all")
    public ResponseEntity<ApiResp<Void>> markAllAsRead() {
        notificationService.markAllAsRead();
        ApiResp<Void> response = ApiResp.<Void>builder()
                .message("All notifications marked as read")
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Xóa thông báo", description = "API xóa một thông báo cụ thể")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResp<Void>> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        ApiResp<Void> response = ApiResp.<Void>builder()
                .message("Notification deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}