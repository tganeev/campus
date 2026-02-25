package com.campus.backend.repository;

import com.campus.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByTelegramNick(String telegramNick);

    @Query("SELECT u FROM User u WHERE u.role = 'USER'")
    List<User> findAllPeers();

    @Query("SELECT u FROM User u JOIN u.clubs c WHERE c.id = :clubId")
    List<User> findByClubId(@Param("clubId") Long clubId);

    boolean existsByEmail(String email);
}