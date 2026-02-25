package com.campus.backend.dto;

import com.campus.backend.model.PresenceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresenceDTO {

    private Long userId;
    private String userName;
    private PresenceStatus status;
    private String location;
    private LocalDateTime lastSeen;
}