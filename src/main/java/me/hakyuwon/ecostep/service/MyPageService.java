package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.domain.UserMission;
import me.hakyuwon.ecostep.dto.CarbonStatsDto;
import me.hakyuwon.ecostep.dto.MissionProgressDto;
import me.hakyuwon.ecostep.dto.MyPageDto;
import me.hakyuwon.ecostep.dto.UserDto;
import me.hakyuwon.ecostep.repository.MissionRepository;
import me.hakyuwon.ecostep.repository.UserMissionRepository;
import me.hakyuwon.ecostep.repository.UserRepository;
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

    public MyPageDto getMyPage(@PathVariable Long userId) {
        // 1. 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
        UserDto profile = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getTree().getTreeName()
        );

        // 2. 탄소 감축량 통계 조회
        List<UserMission> userMissions = (List<UserMission>) userMissionRepository.findByUser(user);
        double totalReduction = userMissions.stream()
                .mapToDouble(m -> m.getCarbonReduction())
                .sum();
        CarbonStatsDto carbonStats = new CarbonStatsDto(userId, totalReduction);

        // 3. 이번 달 미션 달성률 조회
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();

        List<UserMission> eachMissions = userMissionRepository.countByUserAndCompletedAtAfter(user, startOfMonth);
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
}
