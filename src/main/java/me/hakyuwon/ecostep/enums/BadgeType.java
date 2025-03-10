package me.hakyuwon.ecostep.enums;

public enum BadgeType {
    TUMBLER_MASTER("텀블러 선구자", 5),
    RECEIPT_EXPERT("영수증 분석 전문가", 5),
    WALKING_CHAMP("걷기 챔피언", 5),
    ATTENDANCE_REGULAR("출석 체크 완벽", 5),
    QUIZ_MASTER("퀴즈 마스터", 5);

    private final String name; // 뱃지 이름
    private final int requiredCount; // 미션 수행 횟수 (몇 번 해야 뱃지를 받을 수 있는지)

    BadgeType(String name, int requiredCount) {
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
