package me.hakyuwon.ecostep.event.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hakyuwon.ecostep.event.MissionCompletedEvent;
import me.hakyuwon.ecostep.event.handler.BadgeEventHandler;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@RequiredArgsConstructor
public class BadgeConsumer {

    private final BadgeEventHandler badgeEventHandler;

    @KafkaListener(
            topics = "mission.completed",
            groupId = "badge-group"
    )
    public void consume(MissionCompletedEvent event) {

        log.info(
                "배지 처리 이벤트 수신 - eventId: {}, userId: {}, missionId: {}",
                event.eventId(),
                event.userId(),
                event.missionId()
        );

        badgeEventHandler.handle(event);
    }
}
