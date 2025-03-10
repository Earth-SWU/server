package me.hakyuwon.ecostep.repository;

import me.hakyuwon.ecostep.domain.Mission;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.domain.UserMission;
import me.hakyuwon.ecostep.enums.MissionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
    boolean existsByUserAndMissionAndCompletedAtAfter(User user, Mission mission, LocalDateTime startOfDay);
    <Optional> UserMission findByUser (User user);
    long countByUserAndMission(User user, Mission mission);
    List<UserMission> countByUserAndCompletedAtAfter(User user, LocalDateTime startOfDay);
}
