package com.careerconnect.config;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    @EventListener
    public void handleWebSocketConnect(SessionConnectedEvent event) {
        // Update user online status 
    }
 
    @EventListener 
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        // Update user offline status 
    }
}