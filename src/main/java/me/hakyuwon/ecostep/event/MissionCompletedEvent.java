package me.hakyuwon.ecostep.event;

import java.time.LocalDateTime;

public record MissionCompletedEvent(
        Long userId,
        Long missionId,
        LocalDateTime completedAt
) {
}
