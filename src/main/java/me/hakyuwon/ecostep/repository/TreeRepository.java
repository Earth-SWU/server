package me.hakyuwon.ecostep.repository;

import me.hakyuwon.ecostep.domain.Tree;
import me.hakyuwon.ecostep.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TreeRepository extends JpaRepository<Tree, Long> {
    Optional<Tree> findByUser(User user);
}
