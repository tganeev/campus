package com.campus.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {

    @NotBlank(message = "Name is required")
    private String name;

    private String email;  // Больше не обязательный

    private String password;  // Больше не обязательный

    private String telegramNick;

    @NotBlank(message = "School21 login is required")
    private String school21Login;  // Новое поле
}