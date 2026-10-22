package me.hakyuwon.ecostep.event.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hakyuwon.ecostep.event.MissionCompletedEvent;
import me.hakyuwon.ecostep.event.handler.TreeRewardEventHandler;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@RequiredArgsConstructor
public class TreeRewardConsumer {
    private final TreeRewardEventHandler eventHandler;

    @KafkaListener(
            topics = "mission.completed",
            groupId = "tree-reward-group"
    )
    public void consume(MissionCompletedEvent event) {
        log.info(
                "나무 이벤트 수신 - eventId: {}, userId: {}, missionId: {}",
                event.eventId(),
                event.userId(),
                event.missionId()
        );

        eventHandler.handle(event);
    }
}
