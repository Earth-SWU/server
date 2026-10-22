package me.hakyuwon.ecostep.event.handler;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.ProcessedEvent;
import me.hakyuwon.ecostep.event.MissionCompletedEvent;
import me.hakyuwon.ecostep.repository.ProcessedEventRepository;
import me.hakyuwon.ecostep.service.BadgeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BadgeEventHandler {

    private static final String CONSUMER_NAME = "badge";

    private final ProcessedEventRepository processedEventRepository;
    private final BadgeService badgeService;

    @Transactional
    public void handle(MissionCompletedEvent event) {

        boolean alreadyProcessed = processedEventRepository.existsByEventIdAndConsumerName(
                event.eventId(),
                CONSUMER_NAME
        );

        if (alreadyProcessed) {
            return;
        }

        badgeService.checkBadge(event.userId(), event.missionId());

        processedEventRepository.save(new ProcessedEvent(event.eventId(), CONSUMER_NAME));
    }
}