package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.Tree;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.domain.UserMission;
import me.hakyuwon.ecostep.dto.*;
import me.hakyuwon.ecostep.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        // 2. 탄소 감축량 통계 조회
        List<UserMission> userMissions = userMissionRepository.findByUser(user);
        double totalReduction = userMissions.stream()
                .mapToDouble(UserMission::getCarbonReduction)
                .sum();
        CarbonStatsDto carbonStats = new CarbonStatsDto(userId, totalReduction);

        // 3. 이번 달 미션 달성률 조회
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();

        List<UserMission> eachMissions = userMissionRepository.findByUserAndCompletedAtAfter(user, startOfMonth);
        long totalMissions = eachMissions.size();
        // 미션 타입과 각각의 횟수가 저장됨
        Map<String, Long> missionCounts = eachMissions.stream()
                .collect(Collectors.groupingBy(um -> String.valueOf(um.getMission().getMissionType()), Collectors.counting()));

        // 4. 미션별 퍼센트 계산
        Map<String, Double> missionPercentages = missionCounts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (totalMissions > 0) ? (entry.getValue() * 100.0 / totalMissions) : 0.0
                ));

        MissionProgressDto missionProgress = new MissionProgressDto(userId, totalMissions, missionPercentages.size());

        // 4. 통합 DTO 생성
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
