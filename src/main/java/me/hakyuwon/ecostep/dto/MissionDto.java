package me.hakyuwon.ecostep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.hakyuwon.ecostep.enums.MissionType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissionDto {
    private Long userId;
    private MissionType missionType;
    private String description;
    private double carbonReduction;
    private int missionWater;
}
