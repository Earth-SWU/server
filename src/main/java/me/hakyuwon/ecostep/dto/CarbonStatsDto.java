package me.hakyuwon.ecostep.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CarbonStatsDto {
    private Long userId;
    private double carbonReduction;
}
