package me.hakyuwon.ecostep.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MissionProgressDto {
    private Long userId;

    private Long totalCompleted;
    private double missionPercent;
}
