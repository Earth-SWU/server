package me.hakyuwon.ecostep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hakyuwon.ecostep.enums.MissionType;

@Data
@NoArgsConstructor
public class MissionDto {
    private Long id;
    private MissionType missionType;
    private String description;
    private boolean completed;

    public MissionDto(Long id, MissionType missionType, String description, boolean completed) {
        this.id = id;
        this.missionType = missionType;
        this.description = description;
        this.completed = completed;
    }

    @Getter
    public static class MissionBadgeResponseDto{
        private long missionCount;
        private String missionMessage;
        private String badgeMessage;

        public MissionBadgeResponseDto(long missionCount, String missionMessage, String badgeMessage) {
            this.missionCount = missionCount;
            this.missionMessage = missionMessage;
            this.badgeMessage = badgeMessage;
        }
    }
}
