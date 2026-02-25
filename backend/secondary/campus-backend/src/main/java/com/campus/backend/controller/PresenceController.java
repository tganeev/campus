package com.campus.backend.controller;

import com.campus.backend.dto.PresenceDTO;
import com.campus.backend.dto.PresenceUpdateDTO;
import com.campus.backend.security.UserPrincipal;
import com.campus.backend.service.PresenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/presence")
@RequiredArgsConstructor
@Tag(name = "Присутствие", description = "Управление статусами присутствия")
public class PresenceController {

    private final PresenceService presenceService;

    @GetMapping("/online")
    @Operation(summary = "Получить всех онлайн пользователей")
    public ResponseEntity<List<PresenceDTO>> getOnlineUsers() {
        return ResponseEntity.ok(presenceService.getAllOnlineUsers());
    }

    @GetMapping("/online/count")
    @Operation(summary = "Получить количество онлайн пользователей")
    public ResponseEntity<Long> getOnlineCount() {
        return ResponseEntity.ok(presenceService.getOnlineCount());
    }

    @GetMapping("/me")
    @Operation(summary = "Получить свой текущий статус")
    public ResponseEntity<PresenceDTO> getMyPresence(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        PresenceDTO presence = presenceService.getCurrentPresence(userPrincipal.getId());
        return ResponseEntity.ok(presence);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить статус пользователя по ID")
    public ResponseEntity<PresenceDTO> getUserPresence(@PathVariable Long userId) {
        PresenceDTO presence = presenceService.getCurrentPresence(userId);
        return ResponseEntity.ok(presence);
    }

    @PostMapping("/me")
    @Operation(summary = "Обновить свой статус")
    public ResponseEntity<PresenceDTO> updateMyPresence(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody PresenceUpdateDTO dto) {
        return ResponseEntity.ok(presenceService.updatePresence(userPrincipal.getId(), dto));
    }
}