package me.hakyuwon.ecostep.repository;

import me.hakyuwon.ecostep.domain.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
    Optional<UserMission> findById(Long userId, Long missionId);
}
