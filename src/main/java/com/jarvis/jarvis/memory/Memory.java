package com.jarvis.jarvis.memory;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Memory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String content;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Memory() {}
    public Memory(String content) { this.content = content; }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}