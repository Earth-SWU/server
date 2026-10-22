package me.hakyuwon.ecostep.event.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hakyuwon.ecostep.event.MissionCompletedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MissionCompletedConsumer {
    // test
    @KafkaListener(
            topics = "mission.completed",
            groupId = "ecostep-consumer"
    )
    public void consume(MissionCompletedEvent event) {
        log.info(
                "미션 완료 이벤트 수신 - userId: {}, missionId: {}, completedAt: {}",
                event.userId(),
                event.missionId(),
                event.completedAt()
        );
    }
}
