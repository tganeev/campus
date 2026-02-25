package com.campus.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "presence")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private PresenceStatus status = PresenceStatus.OFFLINE;

    private String location;

    private LocalDateTime lastSeen = LocalDateTime.now();

    @Column(updatable = false)
    private LocalDateTime checkIn = LocalDateTime.now();

    private LocalDateTime checkOut;
}