package me.hakyuwon.ecostep.enums;

public enum BadgeType {
    TUMBLER_MASTER("텀블러 선구자", 5),
    RECEIPT_EXPERT("영수증 분석 전문가", 5),
    WALKING_CHAMP("걷기 챔피언", 5),
    ATTENDANCE_ECOSTEP("에코스텝러", 5),
    IAM_BEGINNER("에코스텝 비기너",null),
    IAM_MASTER("에코스텝 마스터",null);

    private final String name; // 뱃지 이름
    private final Integer requiredCount; // 미션 수행 횟수 (몇 번 해야 뱃지를 받을 수 있는지)

    BadgeType(String name, Integer requiredCount) {
        this.name = name;
        this.requiredCount = requiredCount;
    }

    public String getName() {
        return name;
    }

    public int getRequiredCount() {
        return requiredCount;
    }
}
