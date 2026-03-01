package com.campus.backend.controller;

import com.campus.backend.client.School21Client;
import com.campus.backend.dto.AuthResponseDTO;
import com.campus.backend.dto.School21LoginRequest;
import com.campus.backend.dto.UserCreateDTO;
import com.campus.backend.dto.UserDTO;
import com.campus.backend.dto.school21.ParticipantV1DTO;
import com.campus.backend.exception.ResourceNotFoundException;
import com.campus.backend.security.JwtService;
import com.campus.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Аутентификация", description = "Вход через School21")
public class AuthController {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;
    private final School21Client school21Client;

    @PostMapping("/school21")
    @Operation(summary = "Вход/регистрация через School21")
    public ResponseEntity<AuthResponseDTO> school21Login(@Valid @RequestBody School21LoginRequest request) {
        log.info("Attempting School21 login for login: {}", request.getLogin());

        // 1. Проверяем логин в School21
        ParticipantV1DTO school21User = school21Client.getParticipantByLogin(request.getLogin());
        if (school21User == null) {
            log.warn("User with login {} not found in School21", request.getLogin());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. Ищем пользователя в нашей БД по School21 логину
        UserDTO userDTO;
        try {
            userDTO = userService.getUserBySchool21Login(request.getLogin());
            log.info("Existing user found with id: {}", userDTO.getId());
        } catch (ResourceNotFoundException e) {
            // 3. Если пользователь не найден - создаем нового
            log.info("User with login {} not found in local DB. Creating new user.", request.getLogin());

            UserCreateDTO createDTO = UserCreateDTO.builder()
                    .name(school21User.getLogin())  // Используем логин как имя
                    .school21Login(school21User.getLogin())
                    .telegramNick(school21User.getLogin())  // Можно тоже сохранить для обратной совместимости
                    .build();

            userDTO = userService.createUserFromSchool21(createDTO);
            log.info("New user created with id: {}", userDTO.getId());
        }

        // 4. Генерируем JWT для нашего пользователя (используем school21Login вместо email)
        UserDetails userDetails = userDetailsService.loadUserByUsername(userDTO.getSchool21Login());
        String token = jwtService.generateToken(userDetails);

        // 5. Возвращаем ответ
        return ResponseEntity.ok(AuthResponseDTO.builder()
                .token(token)
                .user(userDTO)
                .build());
    }
}