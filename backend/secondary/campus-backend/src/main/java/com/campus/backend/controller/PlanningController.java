package com.campus.backend.controller;

import com.campus.backend.dto.PresencePlanDTO;
import com.campus.backend.service.PlanningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/planning")
@RequiredArgsConstructor
@Tag(name = "Планирование", description = "Управление планами присутствия")
public class PlanningController {

    private final PlanningService planningService;

    @GetMapping("/schedules")
    @Operation(summary = "Получить все планы всех пользователей")
    public ResponseEntity<List<PresencePlanDTO>> getAllSchedules() {
        return ResponseEntity.ok(planningService.getAllSchedules());
    }

    @GetMapping("/schedule/{userId}")
    @Operation(summary = "Получить планы пользователя по ID")
    public ResponseEntity<List<PresencePlanDTO>> getUserSchedules(@PathVariable Long userId) {
        return ResponseEntity.ok(planningService.getUserSchedules(userId));
    }

    @GetMapping("/schedule")
    @Operation(summary = "Получить свои планы")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PresencePlanDTO>> getMySchedules(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(planningService.getUserSchedules(userPrincipal.getId()));
    }

    @PostMapping("/schedule")
    @Operation(summary = "Создать план присутствия")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PresencePlanDTO> createSchedule(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody PresencePlanDTO planDTO) {
        planDTO.setUserId(userPrincipal.getId());
        return ResponseEntity.ok(planningService.createSchedule(planDTO));
    }

    @PutMapping("/schedule/{planId}")
    @Operation(summary = "Обновить план")
    public ResponseEntity<PresencePlanDTO> updateSchedule(
            @PathVariable Long planId,
            @RequestBody PresencePlanDTO planDTO) {
        return ResponseEntity.ok(planningService.updateSchedule(planId, planDTO));
    }

    @DeleteMapping("/schedule/{planId}")
    @Operation(summary = "Удалить план")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long planId) {
        planningService.deleteSchedule(planId);
        return ResponseEntity.noContent().build();
    }
}