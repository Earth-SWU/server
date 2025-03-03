package me.hakyuwon.ecostep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.hakyuwon.ecostep.enums.MissionType;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissionDto {
    private Long userId;
    private MissionType missionType;
    private String description;
    private BigDecimal carbonReduction;
    private int missionWater;
    private int missionFert;
}
