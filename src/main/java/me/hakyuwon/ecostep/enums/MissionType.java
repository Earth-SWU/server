package me.hakyuwon.ecostep.enums;

import lombok.Getter;
import me.hakyuwon.ecostep.domain.Badge;

import java.util.Arrays;

@Getter
public enum MissionType {
    ATTENDANCE(1L, "출석 미션"),
    TUMBLER(2L, "텀블러 사용"),
    WALK(3L, "5000보 이상 걷기"),
    STAIR(4L, "계단 이용하기"),
    OXQUIZ(5L, "ox 퀴즈 풀기"),
    CHOICEQUIZ(6L, "객관식 퀴즈 풀기"),
    ECODIARY(7L,"환경 일기 쓰기");

    private final Long missionId;
    private final String description;

    MissionType(Long missionId, String description) {
        this.missionId = missionId;
        this.description = description;
    }

    public static MissionType fromId(Long missionId) {
        return Arrays.stream(values())
                .filter(type -> type.getMissionId().equals(missionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 MissionType이 없습니다."));
    }

    // MissionType에 맞는 뱃지 타입을 반환하는 메서드
    public BadgeType getBadgeType() {
        switch (this) {
            case ATTENDANCE:
                return BadgeType.ATTENDANCE_ECOSTEP;
            case TUMBLER:
                return BadgeType.TUMBLER_MASTER;
            case WALK:
                return BadgeType.WALKING_CHAMP;
            case STAIR:
                return BadgeType.WALKING_CHAMP;
            default:
                throw new IllegalArgumentException("해당 미션에 맞는 뱃지가 존재하지 않습니다.");
        }
    }

    public String getBadgeName(){
        switch (this) {
            case ATTENDANCE:
                return "에코스텝러";
            case TUMBLER:
                return "텀블러 마스터";
            case WALK:
                return "걷기 챔피언";
            case STAIR:
                return "계단 챔피언";
            default:
                throw new IllegalArgumentException("해당 미션에 맞는 뱃지가 존재하지 않습니다.");
        }
    }

}
