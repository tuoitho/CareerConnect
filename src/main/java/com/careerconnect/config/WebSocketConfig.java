package com.careerconnect.config;

import com.careerconnect.security.CustomUserDetailsService;
import com.careerconnect.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtService tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${allowed.origins}")
    private String allowedOrigins;
    @Override 
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] originsArray = allowedOrigins.split(",");
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
//                .setAllowedOrigins("http://localhost:3000/")
                .setAllowedOrigins(originsArray)
                .withSockJS();
                
        // Thêm endpoint cho tính năng phỏng vấn online
        registry.addEndpoint("/ws-interview")
                .setAllowedOriginPatterns("*")
//                .setAllowedOrigins("http://localhost:3000/")
                .setAllowedOrigins(originsArray)
                .withSockJS();
    }
 
    @Override 
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); 
        registry.enableSimpleBroker("/topic",  "/queue");
        registry.setUserDestinationPrefix("/user");
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new AuthChannelInterceptor(tokenProvider, customUserDetailsService));
        registration.taskExecutor()
                .corePoolSize(8) // Tăng số thread
                .maxPoolSize(16)
                .queueCapacity(100); // Tăng dung lượng hàng đợi
    }
}