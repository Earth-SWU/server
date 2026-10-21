package me.hakyuwon.ecostep.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(
        columnNames = {"eventId", "consumerName"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessedEvent {

    @Id
    private String eventId;

    private String consumerName;

    private LocalDateTime processedAt;

    public ProcessedEvent(String eventId, String consumerName) {
        this.eventId = eventId;
        this.consumerName = consumerName;
        this.processedAt = LocalDateTime.now();
    }
}
