package com.campus.backend.controller;

import com.campus.backend.dto.UserCreateDTO;
import com.campus.backend.dto.UserDTO;
import com.campus.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Пользователи", description = "Управление пользователями")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Получить всех пользователей")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("REST request to get all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/peers")
    @Operation(summary = "Получить всех пиров")
    public ResponseEntity<List<UserDTO>> getPeers() {
        log.info("REST request to get all peers");
        return ResponseEntity.ok(userService.getPeers());
    }

    @GetMapping("/peers/paged")
    @Operation(summary = "Получить пиров с пагинацией")
    public ResponseEntity<Page<UserDTO>> getPeersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("REST request to get peers paged: page={}, size={}", page, size);
        Page<UserDTO> pageResult = userService.getPeersPaged(PageRequest.of(page, size));
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        log.info("REST request to get user by id: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @Operation(summary = "Создать нового пользователя")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO dto) {
        log.info("REST request to create user: {}", dto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Обновить пользователя")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserCreateDTO dto) {
        log.info("REST request to update user with id: {}", id);
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить пользователя")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("REST request to delete user with id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}