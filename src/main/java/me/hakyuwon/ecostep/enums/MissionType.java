package me.hakyuwon.ecostep.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MissionType {
    ATTENDANCE(1L, "출석 미션"),
    RECEIPT(2L, "영수증 인증"),
    TUMBLER(3L, "텀블러 사용"),
    WALK(4L, "3000보 이상 걷기");

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
                return BadgeType.ATTENDANCE_REGULAR;
            case RECEIPT:
                return BadgeType.RECEIPT_EXPERT;
            case TUMBLER:
                return BadgeType.TUMBLER_MASTER;
            case WALK:
                return BadgeType.WALKING_CHAMP;
            default:
                throw new IllegalArgumentException("해당 미션에 맞는 뱃지가 존재하지 않습니다.");
        }
    }
}
