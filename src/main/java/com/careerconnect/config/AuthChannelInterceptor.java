package com.careerconnect.config;

import com.careerconnect.security.CustomUserDetailsService;
import com.careerconnect.security.JwtTokenProvider;
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
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        StompCommand command = accessor.getCommand();
//
//        // Xử lý CONNECT và SEND
//        if (StompCommand.CONNECT.equals(command) || StompCommand.SEND.equals(command)) {
////            String token = accessor.getFirstNativeHeader("Authorization");
////            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
////            String tk2 = (String) ((ArrayList) ((Map) raw).get("Authorization")).get(0);
////            Logger.log("tk2: " + tk2);
////            Logger.log("Command: " + command + ", Token: " + token);
////            try {
////                if (StringUtils.hasText(tk2) && tk2.startsWith("Bearer ")) {
////                    token=tk2;
////                    token = token.substring(7);
////                    if (tokenProvider.validateToken(token)) {
////                        String username = tokenProvider.getUsernameFromToken(token);
////                        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
////                        // Tạo Principal từ UserDetails
////                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
////                                userDetails, null, userDetails.getAuthorities()
////                        );
////                        // Đặt Principal trực tiếp vào accessor
////                        accessor.setUser(new Principal() {
////                            @Override
////                            public String getName() {
////                                return username;
////                            }
////                        });
////                        Logger.log("User authenticated for " + command + ": " + username);
////                    } else {
////                        Logger.log("Token validation failed for " + command);
////                    }
////                } else {
////                    Logger.log("No valid Bearer token found for " + command);
////                }
////            } catch (Exception ex) {
////                Logger.log("Error during WebSocket authentication for " + command + ": " + ex.getMessage());
////            }
//            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//                Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
//
//                if (raw instanceof Map) {
//                    Object name = ((Map) raw).get("username");
//                    if (name instanceof ArrayList) {
//                        accessor.setUser(new User(((ArrayList<String>) name).get(0).toString()));
//                    }
//                }
//                Logger.log("User authenticated for " + command + ": " + accessor.getUser().getName());
//            }
//        }
//        return message;
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
//
//            if (raw instanceof Map) {
//                Object name = ((Map) raw).get("username");
//
//                if (name instanceof ArrayList) {
//                    accessor.setUser(new User(((ArrayList<String>) name).get(0).toString()));
//                }
//            }
//        }
//        return message;
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token;
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
            String tk2 = (String) ((ArrayList) ((Map) raw).get("Authorization")).get(0);
            try {
                if (StringUtils.hasText(tk2) && tk2.startsWith("Bearer ")) {
                    token=tk2;
                    token = token.substring(7);
                    if (tokenProvider.validateToken(token)) {
                        String username = tokenProvider.getUsernameFromToken(token);
                        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                        // Tạo Principal từ UserDetails
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        // Đặt Principal trực tiếp vào accessor
                        accessor.setUser(new Principal() {
                            @Override
                            public String getName() {
                                return username;
                            }
                        });
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
        return message;
    }
}