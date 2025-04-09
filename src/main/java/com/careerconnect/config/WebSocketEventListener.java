package com.careerconnect.config;

import com.careerconnect.dto.chat.UserStatusMessage;
import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    // Lưu danh sách user online (userId)
    private final Set<Long> onlineUsers = new HashSet<>();
    private final SimpMessagingTemplate messagingTemplate; // Inject để gửi thông báo
    //  TODO:  Thông báo thay đổi trạng thái qua WebSocket: Khi một user kết nối hoặc ngắt kết nối, gửi thông báo qua WebSocket để frontend cập nhật trạng thái thời gian thực:

    @EventListener
    public void handleWebSocketConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        if (principal != null) {
            Long userId = Long.valueOf(principal.getName()); // Giả sử Principal chứa userId
            onlineUsers.add(userId);
            // Cập nhật trạng thái online trong DB nếu cần
//            System.out.println("User connected: " + userId);
            Logger.log("User connected: " + userId);
            messagingTemplate.convertAndSend("/topic/userStatus", new UserStatusMessage(userId, true));
        }
    }


    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        if (principal != null) {
            Long userId = Long.valueOf(principal.getName());
            onlineUsers.remove(userId);
            // Cập nhật trạng thái offline trong DB nếu cần
//            System.out.println("User disconnected: " + userId);
            messagingTemplate.convertAndSend("/topic/userStatus", new UserStatusMessage(userId, false));
        }
    }

    // Phương thức kiểm tra user online
    public boolean isUserOnline(Long userId) {
        return onlineUsers.contains(userId);
    }
}