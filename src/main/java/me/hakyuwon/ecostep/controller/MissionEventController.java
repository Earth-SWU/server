package me.hakyuwon.ecostep.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.event.MissionCompletedEvent;
import me.hakyuwon.ecostep.event.producer.MissionEventProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/test/kafka")
@RequiredArgsConstructor
public class MissionEventController {
    private final MissionEventProducer producer;

    @PostMapping("/mission-completed")
    public Map<String, String> publishTestEvent() {
        MissionCompletedEvent event = new MissionCompletedEvent(
                1L,
                100L,
                LocalDateTime.now()
        );

        producer.publish(event);

        return Map.of("message", "Kafka 이벤트 발행 요청 완료");
    }
}
