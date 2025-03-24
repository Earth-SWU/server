package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.*;
import me.hakyuwon.ecostep.dto.MissionDto;
import me.hakyuwon.ecostep.dto.StepDataDto;
import me.hakyuwon.ecostep.enums.BadgeType;
import me.hakyuwon.ecostep.enums.MissionType;
import me.hakyuwon.ecostep.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final BadgeRepository badgeRepository;

    // 미션 목록 조회
    public List<MissionDto> getAllMissions(Long userId) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2. 전체 미션 목록 조회
        List<Mission> missions = missionRepository.findAll();

        // 3. 오늘 달성한 미션 기록 조회
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        List<UserMission> todayMissions = userMissionRepository.findByUserAndCompletedAtAfter(user, startOfToday);

        // 4. 오늘 완료한 미션 id Set 만들기
        Set<Long> completedMissionIds = todayMissions.stream()
                .map(um -> um.getMission().getId())
                .collect(Collectors.toSet());

        // 5. 전체 미션을 순회하며 DTO에 달성 여부 설정
        return missions.stream()
                .map(mission -> new MissionDto(
                        mission.getId(),
                        mission.getMissionType(),
                        mission.getDescription(),
                        completedMissionIds.contains(mission.getId()) // 달성 여부
                ))
                .collect(Collectors.toList());
    }

    // 미션 완료
    @Transactional
    public MissionDto.MissionBadgeResponseDto completeMission(Long userId, Long missionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미션입니다."));
        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 트리가 존재하지 않음"));

        // 오늘 날짜의 시작 시간 (00:00)
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        double carbonReduction = mission.getCarbonReduction();
        String missionMessage;

        boolean alreadyCompleted = userMissionRepository.existsByUserAndMissionAndCompletedAtAfter(user, mission, startOfDay);
        if (!alreadyCompleted) {
            // 미션 완료 기록 생성
            UserMission userMission = UserMission.builder()
                    .user(user)
                    .mission(mission)
                    .carbonReduction(carbonReduction)
                    .completedAt(LocalDateTime.now())
                    .build();

            // 미션 유형에 따른 트리 업데이트
            if (WATER_MISSION_IDS.contains(missionId)) {
                tree.applyItems(1, 0);
            } else if (FERTILIZER_MISSION_IDS.contains(missionId)) {
                tree.applyItems(0, 1);
            }

            userMissionRepository.save(userMission);
            treeRepository.save(tree);
            missionMessage = "미션이 완료되었습니다.";
        } else {
            missionMessage = "이미 완료한 미션입니다.";
        }

        // 해당 미션 수행 횟수 조회
        long missionCount = userMissionRepository.countByUserAndMission(user, mission);
        return new MissionDto.MissionBadgeResponseDto(missionCount, missionMessage);
    }

    // 출석 체크
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

    @Transactional
    public String checkSteps(StepDataDto stepDataDto) {
        User user = userRepository.findById(stepDataDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
        Mission mission = missionRepository.findByMissionType(MissionType.WALK)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 미션입니다."));

        LocalDate today = LocalDate.now();
        boolean alreadyCompleted = userMissionRepository.existsByUserAndMissionAndCompletedAtAfter(user, mission, today.atStartOfDay());

        int step = stepDataDto.getSteps();
        if (step >= 3000) {
            if (alreadyCompleted) {
                return "이미 걸음수 체크를 했어요.";
            }
            return "3000보 이상 걷고, 물 받아요!";
        } else return "아직 걸음수가 모자라요!";
    }

    @Transactional
    public ResponseEntity<String> checkBadge(Long userId, Long missionId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        // 사용자가 해당 미션을 수행한 횟수를 조회
        long missionCount = userMissionRepository.countByUserAndMission(user, mission);

        // 미션 타입에 맞는 뱃지 타입을 가져오기
        BadgeType badgeType = mission.getMissionType().getBadgeType();

        // 미션 수행 횟수가 조건을 만족하는지 확인
        if (missionCount >= badgeType.getRequiredCount()) {
            // 뱃지 중복 인증
            Badge badge = badgeRepository.findByName(mission.getMissionType().getBadgeName())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 뱃지입니다."));

            if (userBadgeRepository.existsByUserAndBadge(user, badge)) {
                return ResponseEntity.ok("이미 받은 뱃지입니다.");
            } else {
                // 조건을 만족하면 뱃지 부여
                Badge newBadge = new Badge();
                newBadge.setBadgeType(badgeType);
                newBadge.setDescription(badgeType.getName() + "을(를) 달성했습니다!");

                UserBadge userBadge = new UserBadge();
                userBadge.setUser(user);
                userBadge.setBadge(newBadge);
                userBadge.setAwardedAt(LocalDate.now());

                userBadgeRepository.save(userBadge);
            }
        }
            return ResponseEntity.ok("뱃지 지급 조건을 충족하지 않았습니다.");
    }
}
