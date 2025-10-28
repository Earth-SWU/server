package me.hakyuwon.ecostep.repository;

import me.hakyuwon.ecostep.domain.Mission;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.domain.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
    boolean existsByUserAndMissionAndCompletedAtAfter(User user, Mission mission, LocalDateTime startOfDay);
    List<UserMission> findByUser (User user);
    long countByUserAndMission(User user, Mission mission);
    int countByUser(User user);
    int countByUserAndCompletedAtBetween(User user, LocalDateTime startOfDay , LocalDateTime endOfDay);
    List<UserMission> findByUserAndCompletedAtBetween(User user, LocalDateTime start, LocalDateTime end);
    List<UserMission> findByUserAndCompletedAtAfter(User user, LocalDateTime startOfDay);
}
