package me.hakyuwon.ecostep.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyPageDto {
    private UserDto profile;
    private CarbonStatsDto carbonStats;
    private MissionProgressDto missionProgress;
}
