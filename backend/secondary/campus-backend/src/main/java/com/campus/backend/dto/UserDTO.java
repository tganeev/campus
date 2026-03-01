package com.campus.backend.dto;

import com.campus.backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String telegramNick;
    private String school21Login;  // Новое поле
    private String avatarUrl;
    private Role role;
    private List<String> clubs;
    private String presenceStatus;
}