package me.hakyuwon.ecostep.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDto {
    private String nickName;
    private int missionCount;
    private int treeLevel;
    private int startDays;
}
