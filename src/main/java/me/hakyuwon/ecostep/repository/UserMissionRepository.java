package me.hakyuwon.ecostep.repository;

import me.hakyuwon.ecostep.domain.Mission;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.domain.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
    boolean existsByUserAndMissionAndCompletedAtAfter(User user, Mission mission, LocalDateTime startOfDay);
    List<UserMission> findByUser (User user);
    long countByUserAndMission(User user, Mission mission);
    List<UserMission> findByUserAndCompletedAtAfter(User user, LocalDateTime startOfDay);
}
