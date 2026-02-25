package com.campus.backend.controller;

import com.campus.backend.dto.AuthResponseDTO;
import com.campus.backend.dto.UserCreateDTO;
import com.campus.backend.dto.UserLoginDTO;
import com.campus.backend.security.JwtService;
import com.campus.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Регистрация и вход в систему")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Вход в систему")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody UserLoginDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        var user = userService.getUserByEmail(request.getEmail());

        return ResponseEntity.ok(AuthResponseDTO.builder()
                .token(token)
                .user(userService.getUserById(user.getId()))
                .build());
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody UserCreateDTO request) {
        var user = userService.createUser(request);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(AuthResponseDTO.builder()
                .token(token)
                .user(user)
                .build());
    }
}