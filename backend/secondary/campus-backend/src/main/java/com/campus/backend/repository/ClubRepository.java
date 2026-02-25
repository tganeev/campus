package com.campus.backend.repository;

import com.campus.backend.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {

    Optional<Club> findByName(String name);

    @Query("SELECT c FROM Club c WHERE SIZE(c.members) > 0")
    List<Club> findActiveClubs();

    @Query("SELECT c FROM Club c ORDER BY SIZE(c.members) DESC")
    List<Club> findAllOrderByMembersCount();
}