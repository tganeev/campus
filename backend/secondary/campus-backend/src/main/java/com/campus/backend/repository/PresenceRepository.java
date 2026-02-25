package com.campus.backend.repository;

import com.campus.backend.model.Presence;
import com.campus.backend.model.PresenceStatus;
import com.campus.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PresenceRepository extends JpaRepository<Presence, Long> {

    Optional<Presence> findByUser(User user);

    List<Presence> findByStatus(PresenceStatus status);

    @Query("SELECT p FROM Presence p WHERE p.status = 'ONLINE'")
    List<Presence> findAllOnline();

    @Query("SELECT p FROM Presence p WHERE p.lastSeen > :since")
    List<Presence> findRecentActivity(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(p) FROM Presence p WHERE p.status = 'ONLINE'")
    long countOnlineUsers();
}