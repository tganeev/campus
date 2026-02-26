package com.campus.backend.service;

import com.campus.backend.dto.PresencePlanDTO;
import com.campus.backend.exception.ResourceNotFoundException;
import com.campus.backend.model.PresencePlan;
import com.campus.backend.model.User;
import com.campus.backend.repository.PresencePlanRepository;
import com.campus.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanningService {

    private final PresencePlanRepository planRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public List<PresencePlanDTO> getAllSchedules() {
        return planRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PresencePlanDTO> getUserSchedules(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return planRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PresencePlanDTO createSchedule(PresencePlanDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        PresencePlan plan = new PresencePlan();
        plan.setUser(user);
        // Преобразуем Integer в DayOfWeek (0 = ПН, 1 = ВТ, и т.д.)
        plan.setDayOfWeek(convertIntToDayOfWeek(dto.getDayOfWeek()));
        plan.setStartTime(LocalTime.parse(dto.getStartTime()));
        plan.setEndTime(LocalTime.parse(dto.getEndTime()));
        plan.setRecurring(dto.getRecurring() != null ? dto.getRecurring() : true);

        PresencePlan savedPlan = planRepository.save(plan);
        return convertToDTO(savedPlan);
    }

    @Transactional
    public PresencePlanDTO updateSchedule(Long planId, PresencePlanDTO dto) {
        PresencePlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + planId));

        plan.setDayOfWeek(convertIntToDayOfWeek(dto.getDayOfWeek()));
        plan.setStartTime(LocalTime.parse(dto.getStartTime()));
        plan.setEndTime(LocalTime.parse(dto.getEndTime()));
        plan.setRecurring(dto.getRecurring() != null ? dto.getRecurring() : plan.isRecurring());

        PresencePlan updatedPlan = planRepository.save(plan);
        return convertToDTO(updatedPlan);
    }

    @Transactional
    public void deleteSchedule(Long planId) {
        if (!planRepository.existsById(planId)) {
            throw new ResourceNotFoundException("Plan not found with id: " + planId);
        }
        planRepository.deleteById(planId);
    }

    private PresencePlanDTO convertToDTO(PresencePlan plan) {
        return PresencePlanDTO.builder()
                .id(plan.getId())
                .userId(plan.getUser().getId())
                .userName(plan.getUser().getName())
                .dayOfWeek(convertDayOfWeekToInt(plan.getDayOfWeek()))
                .startTime(plan.getStartTime().format(TIME_FORMATTER))
                .endTime(plan.getEndTime().format(TIME_FORMATTER))
                .recurring(plan.isRecurring())
                .build();
    }

    // Вспомогательные методы для преобразования
    private DayOfWeek convertIntToDayOfWeek(Integer day) {
        if (day == null) return DayOfWeek.MONDAY;
        switch (day) {
            case 0: return DayOfWeek.MONDAY;
            case 1: return DayOfWeek.TUESDAY;
            case 2: return DayOfWeek.WEDNESDAY;
            case 3: return DayOfWeek.THURSDAY;
            case 4: return DayOfWeek.FRIDAY;
            case 5: return DayOfWeek.SATURDAY;
            case 6: return DayOfWeek.SUNDAY;
            default: return DayOfWeek.MONDAY;
        }
    }

    private Integer convertDayOfWeekToInt(DayOfWeek day) {
        if (day == null) return 0;
        switch (day) {
            case MONDAY: return 0;
            case TUESDAY: return 1;
            case WEDNESDAY: return 2;
            case THURSDAY: return 3;
            case FRIDAY: return 4;
            case SATURDAY: return 5;
            case SUNDAY: return 6;
            default: return 0;
        }
    }
}