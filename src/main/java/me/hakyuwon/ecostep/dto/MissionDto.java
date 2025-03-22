package me.hakyuwon.ecostep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.hakyuwon.ecostep.enums.MissionType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissionDto {
    private Long id;
    private MissionType missionType;
    private String description;
    private double carbonReduction;
    private int missionWater;
    private boolean completed;

    public MissionDto(Long id, MissionType missionType, String description, boolean completed) {
        this.id = id;
        this.missionType = missionType;
        this.description = description;
        this.completed = completed;
    }
}
