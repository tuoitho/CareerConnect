package com.careerconnect.config;

import com.careerconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Component
public class WebSocketEventListener {
    // Lưu danh sách user online (userId)
    private final Set<Long> onlineUsers = new HashSet<>();

    @EventListener
    public void handleWebSocketConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        if (principal != null) {
            Long userId = Long.valueOf(principal.getName()); // Giả sử Principal chứa userId
            onlineUsers.add(userId);
            // Cập nhật trạng thái online trong DB nếu cần
            System.out.println("User connected: " + userId);
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
            System.out.println("User disconnected: " + userId);
        }
    }

    // Phương thức kiểm tra user online
    public boolean isUserOnline(Long userId) {
        return onlineUsers.contains(userId);
    }
}