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
        plan.setDayOfWeek(dto.getDayOfWeek());
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

        plan.setDayOfWeek(dto.getDayOfWeek());
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
                .dayOfWeek(plan.getDayOfWeek())
                .startTime(plan.getStartTime().format(TIME_FORMATTER))
                .endTime(plan.getEndTime().format(TIME_FORMATTER))
                .recurring(plan.isRecurring())
                .build();
    }
}