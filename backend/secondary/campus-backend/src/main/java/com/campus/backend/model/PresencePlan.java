package com.campus.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "presence_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresencePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Изменяем аннотацию для правильного преобразования
    @Enumerated(EnumType.STRING)  // Сохраняем как строку (MONDAY, TUESDAY...)
    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "is_recurring")
    private boolean isRecurring = true;
}