package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.Tree;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.domain.UserMission;
import me.hakyuwon.ecostep.dto.*;
import me.hakyuwon.ecostep.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final UserMissionRepository userMissionRepository;
    private final MissionRepository missionRepository;
    private final TreeRepository treeRepository;
    private final UserBadgeRepository userBadgeRepository;

    public MyPageDto getMyPage(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        // user 트리네임 겟, 뱃지 개수, 미션 수행 개수, 나무레벨 겟
        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(()-> new IllegalArgumentException("유효하지 않은 나무입니다."));
        String treeName = tree.getTreeName();
        int badgeCount = userBadgeRepository.countByUser(user);
        int missionCount = userMissionRepository.countByUser(user);
        int treeLevel = tree.getTreeLevel();

        ProfileDto profile = new ProfileDto(treeName, badgeCount, missionCount, treeLevel);

        // 탄소 감축량 통계 조회
        List<UserMission> userMissions = userMissionRepository.findByUser(user);
        BigDecimal totalReduction = userMissions.stream()
                .map(userMission -> BigDecimal.valueOf(userMission.getCarbonReduction()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        CarbonStatsDto carbonStats = new CarbonStatsDto(userId, totalReduction);

        // 이번 달 미션 달성률 조회
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();

        List<UserMission> eachMissions = userMissionRepository.findByUserAndCompletedAtAfter(user, startOfMonth);
        long totalMissions = eachMissions.size();

        // 미션 타입과 각각의 횟수가 저장됨
        Map<String, Long> missionCounts = eachMissions.stream()
                .collect(Collectors.groupingBy(um -> String.valueOf(um.getMission().getMissionType()), Collectors.counting()));

        Map<String, Double> missionPercentages = new HashMap<>();

        if (totalMissions > 0) {
            double sumPercentage = 0.0;
            String maxKey = null;
            double maxPercentage = 0.0;

            // 각 미션의 원래 퍼센트 계산
            for (Map.Entry<String, Long> entry : missionCounts.entrySet()) {
                double percentage = entry.getValue() * 100.0 / totalMissions;
                missionPercentages.put(entry.getKey(), percentage);
                sumPercentage += percentage;

                // 가장 큰 퍼센트를 가진 미션 찾기
                if (percentage > maxPercentage) {
                    maxPercentage = percentage;
                    maxKey = entry.getKey();
                }
            }

            // 소수점 오차 보정: 총합이 100이 아닐 경우, 가장 큰 비율의 미션에 차이를 더함
            double diff = 100.0 - sumPercentage;
            if (maxKey != null) {
                missionPercentages.put(maxKey, missionPercentages.get(maxKey) + diff);
            }
        } else {
            // 미션을 하나도 수행하지 않았을 경우 모든 타입에 대해 0%로 처리
            missionCounts.keySet().forEach(key -> missionPercentages.put(key, 0.0));
        }

        MissionProgressDto missionProgress = new MissionProgressDto(
                userId,
                totalMissions,
                missionPercentages.size(), // 이 값은 미션 타입의 개수를 나타냅니다.
                missionPercentages
        );

        return new MyPageDto(profile, carbonStats, missionProgress);
    }

    public ProfileDto getProfile(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
        // user 트리네임 겟, 뱃지 개수, 미션 수행 개수, 나무레벨 겟
        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(()-> new IllegalArgumentException("유효하지 않은 나무입니다."));

        String treeName = tree.getTreeName();
        int badgeCount = userBadgeRepository.countByUser(user);
        int missionCount = userMissionRepository.countByUser(user);
        int treeLevel = tree.getTreeLevel();

        return ProfileDto.builder()
                .treeName(treeName)
                .badgeCount(badgeCount)
                .missionCount(missionCount)
                .treeLevel(treeLevel)
                .build();
    }
}
