package com.campus.backend.repository;

import com.campus.backend.model.PresencePlan;
import com.campus.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface PresencePlanRepository extends JpaRepository<PresencePlan, Long> {

    List<PresencePlan> findByUser(User user);

    // Оставляем ТОЛЬКО ОДИН метод - с @Query
    @Query("SELECT p FROM PresencePlan p WHERE p.dayOfWeek = :dayOfWeek")
    List<PresencePlan> findByDayOfWeek(@Param("dayOfWeek") DayOfWeek dayOfWeek);

    void deleteByUser(User user);
}