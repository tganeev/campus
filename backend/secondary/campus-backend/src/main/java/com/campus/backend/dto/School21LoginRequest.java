// FILE: src/main/java/com/campus/backend/dto/School21LoginRequest.java
package com.campus.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class School21LoginRequest {
    @NotBlank(message = "Login is required")
    private String login;
}