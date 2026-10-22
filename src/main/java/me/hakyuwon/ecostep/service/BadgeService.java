package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.Badge;
import me.hakyuwon.ecostep.domain.Mission;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.domain.UserBadge;
import me.hakyuwon.ecostep.dto.BadgeDto;
import me.hakyuwon.ecostep.enums.BadgeType;
import me.hakyuwon.ecostep.exception.CustomException;
import me.hakyuwon.ecostep.exception.ErrorCode;
import me.hakyuwon.ecostep.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;
    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    public List<BadgeDto> getAllBadges(Long userId) {
        List<Badge> badges = badgeRepository.findAll();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 사용자가 획득한 뱃지 목록 조회
        Set<Long> acquiredBadgeIds = userBadgeRepository.findByUser(user).stream()
                .map(userBadge -> userBadge.getBadge().getId())
                .filter(Objects::nonNull) // badge가 null일 경우 방지
                .collect(Collectors.toSet());

        // 뱃지 DTO 변환 (획득 여부 포함)
        return badges.stream()
                .map(badge -> new BadgeDto(badge.getId(), badge.getName(), acquiredBadgeIds.contains(badge.getId())))
                .collect(Collectors.toList());

    }

    // 배지 체크 및 부여
    public String checkBadge(Long userId, Long missionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        // 사용자가 해당 미션을 수행한 횟수를 조회
        long missionCount = userMissionRepository.countByUserAndMission(user, mission);

        // 미션 타입에 맞는 배지 타입을 가져오기
        BadgeType badgeType = mission.getMissionType().getBadgeType();

        // 미션 수행 횟수가 조건을 만족하는지 확인
        if (missionCount < badgeType.getRequiredCount()) {
            return "뱃지 지급 조건을 충족하지 않았습니다.";
        }

        Badge badge = badgeRepository.findByName(mission.getMissionType().getBadgeName())
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        if (userBadgeRepository.existsByUserAndBadge(user, badge)) {
            return "이미 획득한 배지입니다.";
        }

        // 사용자와 기존 배지를 연결
        UserBadge userBadge = UserBadge.builder()
                .badge(badge)
                .awardedAt(LocalDate.now(KOREA_ZONE))
                .build();

        user.addBadges(userBadge);
        return "배지를 획득했습니다.";
    }
}
