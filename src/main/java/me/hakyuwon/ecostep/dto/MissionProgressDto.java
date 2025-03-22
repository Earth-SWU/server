package me.hakyuwon.ecostep.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MissionProgressDto {
    private Long userId;
    private Long totalCompleted;
    private int missionTypeCount; // 미션 타입의 개수 (missionPercentages.size())
    private Map<String, Double> missionPercentages; // 미션별 퍼센티지
}
