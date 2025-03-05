package me.hakyuwon.ecostep.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hakyuwon.ecostep.domain.Mission;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserMissionDto {
    private Long userId;
    private Long missionId;
}
