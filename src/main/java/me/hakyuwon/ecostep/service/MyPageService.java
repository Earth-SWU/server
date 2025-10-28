package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.Tree;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.domain.UserMission;
import me.hakyuwon.ecostep.dto.*;
import me.hakyuwon.ecostep.exception.CustomException;
import me.hakyuwon.ecostep.exception.ErrorCode;
import me.hakyuwon.ecostep.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final UserMissionRepository userMissionRepository;
    private final TreeRepository treeRepository;
    private final UserBadgeRepository userBadgeRepository;

    @Transactional
    public MyPageDto getMyPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(()-> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        LocalDate signupDate = user.getCreatedAt().toLocalDate(); // User의 가입일

        long startDays = ChronoUnit.DAYS.between(signupDate, today) + 1;
        String nickname = user.getNickname();
        int missionCount = userMissionRepository.countByUserAndCompletedAtBetween(user, startOfDay, endOfDay);
        int treeLevel = tree.getTreeLevel();

        ProfileDto profile = new ProfileDto(nickname, missionCount, treeLevel, (int) startDays);

        // 탄소 감축량 통계 조회
        List<UserMission> userMissions = userMissionRepository.findByUser(user);
        BigDecimal totalReduction = userMissions.stream()
                .map(userMission -> BigDecimal.valueOf(userMission.getCarbonReduction()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        CarbonStatsDto carbonStats = new CarbonStatsDto(userId, totalReduction);

        // 이번 주 미션 달성률 조회
        LocalDate firstDayOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        LocalDateTime startOfWeek = firstDayOfWeek.atStartOfDay();

        // 이번 주 끝 (일요일 23:59:59)
        LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(6);
        LocalDateTime endOfWeek = lastDayOfWeek.atTime(23, 59, 59);

        List<UserMission> eachMissions = userMissionRepository
                .findByUserAndCompletedAtBetween(user, startOfWeek, endOfWeek);
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

            double diff = 100.0 - sumPercentage;
            if (maxKey != null) {
                missionPercentages.put(maxKey, missionPercentages.get(maxKey) + diff);
            }
        } else {
            missionCounts.keySet().forEach(key -> missionPercentages.put(key, 0.0));
        }

        MissionProgressDto missionProgress = new MissionProgressDto(
                userId,
                totalMissions,
                missionPercentages.size(),
                missionPercentages
        );

        return new MyPageDto(profile, carbonStats, missionProgress);
    }

    @Transactional
    public ProfileDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(()-> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        String nickName = user.getNickname();
        int missionCount = userMissionRepository.countByUserAndCompletedAtBetween(user, startOfDay, endOfDay);
        int treeLevel = tree.getTreeLevel();
        long startDays = ChronoUnit.DAYS.between(startOfDay, today)+ 1;

        return ProfileDto.builder()
                .nickName(nickName)
                .missionCount(missionCount)
                .treeLevel(treeLevel)
                .startDays((int) startDays)
                .build();
    }

    // 숲을 시작한 지 몇 일~
    public ForestDaysResponse getDays(User user){
        LocalDate signupDate = user.getCreatedAt().toLocalDate();
        LocalDate today = LocalDate.now();

        long days = ChronoUnit.DAYS.between(signupDate, today) + 1;

        return ForestDaysResponse.builder()
                .days((int) days)
                .build();
    }
}
