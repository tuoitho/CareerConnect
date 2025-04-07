package com.careerconnect.cv.model;

import jakarta.persistence.*;

@Entity
public class UserCV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    @Column(columnDefinition = "text")
    private String content;
    private String templateId;
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
}