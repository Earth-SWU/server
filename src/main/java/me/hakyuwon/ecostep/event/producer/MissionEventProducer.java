package me.hakyuwon.ecostep.event.producer;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.event.MissionCompletedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MissionEventProducer {
    private final KafkaTemplate<String, MissionCompletedEvent>
            kafkaTemplate;

    public void publish(MissionCompletedEvent event) {
        kafkaTemplate.send(
                "mission.completed",
                event.userId().toString(),
                event
        );
    }
}
