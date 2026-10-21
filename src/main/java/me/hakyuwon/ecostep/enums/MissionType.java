package me.hakyuwon.ecostep.enums;

import lombok.Getter;
import me.hakyuwon.ecostep.exception.CustomException;
import me.hakyuwon.ecostep.exception.ErrorCode;

@Getter
public enum MissionType {
    ATTENDANCE(1L, "출석 미션",RewardType.WATER),
    TUMBLER(2L, "텀블러 사용",RewardType.FERTILIZER),
    WALK(3L, "5000보 이상 걷기",RewardType.WATER),
    STAIR(4L, "계단 이용하기",RewardType.FERTILIZER),
    OXQUIZ(5L, "ox 퀴즈 풀기",RewardType.WATER),
    CHOICEQUIZ(6L, "객관식 퀴즈 풀기",RewardType.WATER),
    ECODIARY(7L,"환경 일기 쓰기",RewardType.FERTILIZER);

    private final Long missionId;
    private final String description;
    private final RewardType rewardType;

    MissionType(Long missionId, String description, RewardType rewardType) {
        this.missionId = missionId;
        this.description = description;
        this.rewardType = rewardType;
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
                return BadgeType.STAIR_CHAMP;
            default:
                throw new CustomException(ErrorCode.BADGE_NOT_FOUND);
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
                throw new CustomException(ErrorCode.BADGE_NOT_FOUND);
        }
    }

}
