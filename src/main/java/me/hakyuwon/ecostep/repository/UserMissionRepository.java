package me.hakyuwon.ecostep.repository;

import me.hakyuwon.ecostep.domain.UserMission;
import me.hakyuwon.ecostep.enums.MissionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
    boolean existsByUserIdAndMission_MissionTypeAndCompletedAtAfter(Long userId, MissionType missionType, LocalDateTime startOfDay);
}
