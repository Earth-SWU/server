package me.hakyuwon.ecostep.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.*;
import me.hakyuwon.ecostep.enums.BadgeType;
import me.hakyuwon.ecostep.enums.MissionType;
import me.hakyuwon.ecostep.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;
    private final UserRepository userRepository;
    private final TreeRepository treeRepository;
    private final UserBadgeRepository userBadgeRepository;

    // 물 주는 미션 ID 목록
    private static final Set<Long> WATER_MISSION_IDS = Set.of(1L, 2L, 3L, 4L);
    // 비료 주는 미션 ID 목록
    private static final Set<Long> FERTILIZER_MISSION_IDS = Set.of(5L);


    @Transactional
    public String completeMission(Long userId, Long missionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미션입니다."));

        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 트리가 존재하지 않음"));

        // 오늘 날짜의 시작 시간 (00:00)
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        // 오늘 해당 미션을 이미 완료했는지 확인
        if (userMissionRepository.existsByUserAndMissionAndCompletedAtAfter(user, mission, startOfDay)) {
            return "이미 완료한 미션입니다.";
        }

        UserMission userMission = UserMission.builder()
                .user(user)
                .mission(mission)
                .completedAt(LocalDateTime.now())
                .build();

        if (WATER_MISSION_IDS.contains(missionId)) {
            tree.applyItems(1,0);
        } else if (FERTILIZER_MISSION_IDS.contains(missionId)) {
            tree.applyItems(0,1);
        }

        userMissionRepository.save(userMission);
        treeRepository.save(tree);
        return "미션 완료!";
    }

    @Transactional
    public String checkAttendance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
        Mission mission = missionRepository.findByMissionType(MissionType.ATTENDANCE)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 미션입니다."));

        LocalDate today = LocalDate.now();
        boolean alreadyCompleted = userMissionRepository.existsByUserAndMissionAndCompletedAtAfter(user, mission, today.atStartOfDay());

        if (alreadyCompleted) {
            return "이미 출석을 했어요.";
        }
        return "매일 출석하고 물 받아요!";
    }

    public void checkBadge(Long userId, Long missionId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        // 사용자가 해당 미션을 수행한 횟수를 조회
        long missionCount = userMissionRepository.countByUserAndMission(user, mission);

        // 미션 타입에 맞는 뱃지 타입을 가져오기
        BadgeType badgeType = mission.getMissionType().getBadgeType(); // Mission의 타입을 기준으로 뱃지 타입 결정

        // 미션 수행 횟수가 조건을 만족하는지 확인
        if (missionCount >= badgeType.getRequiredCount()) {
            // 조건을 만족하면 뱃지 부여
            Badge badge = new Badge();
            badge.setType(badgeType);
            badge.setDescription(badgeType.getName() + "을(를) 달성했습니다!");

            // 유저와 뱃지 연결
            UserBadge userBadge = new UserBadge();
            userBadge.setUser(user);
            userBadge.setBadge(badge);
            userBadge.setAwardedAt(LocalDate.now());

            userBadgeRepository.save(userBadge);
        }
    }
}
