package me.hakyuwon.ecostep.repository;

import me.hakyuwon.ecostep.domain.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
    boolean existsByEventIdAndConsumerName(
            String eventId,
            String consumerName
    );
}
