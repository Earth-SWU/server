package me.hakyuwon.ecostep.repository;

import me.hakyuwon.ecostep.domain.EcoDiary;
import me.hakyuwon.ecostep.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EcoDiaryRepository extends JpaRepository<EcoDiary, Long> {
    Optional<EcoDiary> findByUser(User user);
    EcoDiary save(EcoDiary ecoDiary);
    Optional<EcoDiary> findById(Long id);
    boolean existsByUserAndDate(User user, LocalDate date);
}
