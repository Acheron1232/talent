package com.acheron.mailserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "to_email")
    private String to;
    @Column(name = "from_email")
    private String from;
    private String subject;
    private String content;
    @Column(name = "user_id")
    private Long userId;
}
