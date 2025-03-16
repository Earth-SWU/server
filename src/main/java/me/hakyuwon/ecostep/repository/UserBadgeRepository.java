package me.hakyuwon.ecostep.repository;

import me.hakyuwon.ecostep.domain.Badge;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.domain.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUser(User user);
    int countByUser(User user);
    boolean existsByUserAndBadge(User user, Badge badge);
}
