package com.campus.backend.dto;

import com.campus.backend.model.PresenceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PresenceUpdateDTO {

    @NotNull(message = "Status is required")
    private PresenceStatus status;

    private String location;
}