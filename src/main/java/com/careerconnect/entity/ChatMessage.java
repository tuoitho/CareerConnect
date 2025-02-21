package com.careerconnect.entity;

import com.careerconnect.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private MessageStatus status; // SENT, DELIVERED, READ
}