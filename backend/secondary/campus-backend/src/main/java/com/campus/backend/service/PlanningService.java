package com.campus.backend.service;

import com.campus.backend.dto.PresencePlanDTO;
import com.campus.backend.exception.ResourceNotFoundException;
import com.campus.backend.model.PresencePlan;
import com.campus.backend.model.User;
import com.campus.backend.repository.PresencePlanRepository;
import com.campus.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanningService {

    private final PresencePlanRepository planRepository;
    private final UserRepository userRepository;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public List<PresencePlanDTO> getAllSchedules() {
        log.info("Getting all schedules");
        try {
            List<PresencePlan> plans = planRepository.findAll();
            log.info("Found {} plans", plans.size());

            return plans.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error getting schedules", e);
            throw e;
        }
    }

    public List<PresencePlanDTO> getUserSchedules(Long userId) {
        log.info("Getting schedules for user: {}", userId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

            List<PresencePlan> plans = planRepository.findByUser(user);
            log.info("Found {} plans for user {}", plans.size(), userId);

            return plans.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error getting user schedules for user: {}", userId, e);
            throw e;
        }
    }

    @Transactional
    public PresencePlanDTO createSchedule(PresencePlanDTO dto) {
        log.info("Creating schedule for user: {}", dto.getUserId());
        try {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

            PresencePlan plan = new PresencePlan();
            plan.setUser(user);
            plan.setDayOfWeek(convertIntToDayOfWeek(dto.getDayOfWeek()));
            plan.setStartTime(LocalTime.parse(dto.getStartTime()));
            plan.setEndTime(LocalTime.parse(dto.getEndTime()));
            plan.setRecurring(dto.getRecurring() != null ? dto.getRecurring() : true);

            PresencePlan savedPlan = planRepository.save(plan);
            log.info("Created schedule with id: {}", savedPlan.getId());

            return convertToDTO(savedPlan);

        } catch (Exception e) {
            log.error("Error creating schedule", e);
            throw e;
        }
    }

    @Transactional
    public PresencePlanDTO updateSchedule(Long planId, PresencePlanDTO dto) {
        log.info("Updating schedule: {}", planId);
        try {
            PresencePlan plan = planRepository.findById(planId)
                    .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + planId));

            plan.setDayOfWeek(convertIntToDayOfWeek(dto.getDayOfWeek()));
            plan.setStartTime(LocalTime.parse(dto.getStartTime()));
            plan.setEndTime(LocalTime.parse(dto.getEndTime()));
            plan.setRecurring(dto.getRecurring() != null ? dto.getRecurring() : plan.isRecurring());

            PresencePlan updatedPlan = planRepository.save(plan);
            log.info("Updated schedule: {}", planId);

            return convertToDTO(updatedPlan);

        } catch (Exception e) {
            log.error("Error updating schedule: {}", planId, e);
            throw e;
        }
    }

    @Transactional
    public void deleteSchedule(Long planId) {
        log.info("Deleting schedule: {}", planId);
        try {
            if (!planRepository.existsById(planId)) {
                throw new ResourceNotFoundException("Plan not found with id: " + planId);
            }
            planRepository.deleteById(planId);
            log.info("Deleted schedule: {}", planId);

        } catch (Exception e) {
            log.error("Error deleting schedule: {}", planId, e);
            throw e;
        }
    }

    private PresencePlanDTO convertToDTO(PresencePlan plan) {
        log.debug("Converting plan ID: {}", plan.getId());
        try {
            return PresencePlanDTO.builder()
                    .id(plan.getId())
                    .userId(plan.getUser().getId())
                    .userName(plan.getUser().getName())
                    .dayOfWeek(convertDayOfWeekToInt(plan.getDayOfWeek()))
                    .startTime(plan.getStartTime().format(TIME_FORMATTER))
                    .endTime(plan.getEndTime().format(TIME_FORMATTER))
                    .recurring(plan.isRecurring())
                    .build();
        } catch (Exception e) {
            log.error("Error converting plan: {}", plan.getId(), e);
            throw e;
        }
    }

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