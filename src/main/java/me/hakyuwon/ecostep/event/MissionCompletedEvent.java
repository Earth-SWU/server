package me.hakyuwon.ecostep.event;

import java.time.LocalDateTime;

public record MissionCompletedEvent(
        String eventId,
        Long userId,
        Long missionId,
        LocalDateTime completedAt
) {
}
