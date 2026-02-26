package com.campus.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresencePlanDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Integer dayOfWeek; // 0-6 (Пн-Вс)
    private String startTime;
    private String endTime;
    private Boolean recurring;
}