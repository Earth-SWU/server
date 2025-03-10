package me.hakyuwon.ecostep.repository;

import me.hakyuwon.ecostep.domain.Mission;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.domain.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

}
