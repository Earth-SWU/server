package me.hakyuwon.ecostep.repository;

import me.hakyuwon.ecostep.domain.Badge;
import me.hakyuwon.ecostep.enums.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Optional<Badge> findbyBadgeType(BadgeType badgeType);
    boolean existsByBadgeType(BadgeType badgeType);
}
