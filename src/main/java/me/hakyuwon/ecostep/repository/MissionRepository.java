package me.hakyuwon.ecostep.repository;

import me.hakyuwon.ecostep.domain.Mission;
import me.hakyuwon.ecostep.enums.MissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
    Optional<Mission> findById(Long id);
}
