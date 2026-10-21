package me.hakyuwon.ecostep.event.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hakyuwon.ecostep.event.MissionCompletedEvent;
import me.hakyuwon.ecostep.service.TreeService;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@RequiredArgsConstructor
public class TreeRewardConsumer {
    private final TreeService treeService;

    @KafkaListener(
            topics = "mission.completed",
            groupId = "tree-reward-group"
    )
    public void consume(MissionCompletedEvent event) {
        log.info(
                "나무 보상 처리 - userId: {}, missionId: {}",
                event.userId(),
                event.missionId()
        );

        treeService.applyReward(
                event.userId(),
                event.missionId()
        );
    }
}
