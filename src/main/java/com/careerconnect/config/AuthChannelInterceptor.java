package com.careerconnect.config;

import com.careerconnect.security.CustomUserDetails;
import com.careerconnect.security.CustomUserDetailsService;
import com.careerconnect.security.JwtService;
import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;

@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {
    private final JwtService tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        try {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            if (StompCommand.CONNECT.equals(accessor.getCommand())
//            || StompCommand.SEND.equals(accessor.getCommand())
            ) {
                Logger.log("CONNECT received with token: " + accessor.getNativeHeader("Authorization"));
                Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
                String tk2 = (String) ((ArrayList) ((Map) raw).get("Authorization")).get(0);
                try {
                    if (StringUtils.hasText(tk2) && tk2.startsWith("Bearer ")) {
                        tk2 = tk2.substring(7);
                        //lúc này trong security context chưa có user
                        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(tokenProvider.extractUsername(tk2));
                        if (tokenProvider.isTokenValid(tk2, customUserDetails)) {
                            // Đặt Principal trực tiếp vào accessor
                            accessor.setUser(() -> customUserDetails.getUserId().toString());
                        } else {
                            Logger.log("Token validation failed for " + accessor.getCommand());
                        }
                    } else {
                        Logger.log("No valid Bearer token found for " + accessor.getCommand());
                    }
                } catch (Exception ex) {
                    Logger.log("Error during WebSocket authentication for " + accessor.getCommand() + ": " + ex.getMessage());
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }
}