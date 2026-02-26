package com.campus.backend.repository;

import com.campus.backend.model.PresencePlan;
import com.campus.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresencePlanRepository extends JpaRepository<PresencePlan, Long> {
    List<PresencePlan> findByUser(User user);
    List<PresencePlan> findByDayOfWeek(Integer dayOfWeek);
    void deleteByUser(User user);
}