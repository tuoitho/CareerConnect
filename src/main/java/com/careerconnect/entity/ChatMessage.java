package com.careerconnect.entity;

import com.careerconnect.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User recipient;
    
    private String content;
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MessageStatus status=MessageStatus.SENT; // SENT, DELIVERED, READ
}