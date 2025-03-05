package me.hakyuwon.ecostep.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MissionType {
    ATTENDANCE(1L, "출석 미션"),
    RECEIPT(2L, "영수증 인증"),
    TUMBLER(3L, "텀블러 사용"),
    QUIZ(4L, "OX 퀴즈"),
    ECO_PURCHASE(5L, "친환경 물품 구매"),
    ECO_ARTICLE(6L, "친환경 기사 읽기");

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
}
