package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.*;
import me.hakyuwon.ecostep.dto.MissionDto;
import me.hakyuwon.ecostep.dto.StepDataDto;
import me.hakyuwon.ecostep.enums.BadgeType;
import me.hakyuwon.ecostep.enums.MissionType;
import me.hakyuwon.ecostep.exception.CustomException;
import me.hakyuwon.ecostep.exception.ErrorCode;
import me.hakyuwon.ecostep.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 전체 미션 목록
        List<Mission> missions = missionRepository.findAll();

        // (원래는 오늘 날짜에 완료된 ID를 뽑지만)
        // 테스트용: 빈 Set으로 만들어 항상 미완료 상태
        Set<Long> completedMissionIds = Collections.emptySet();

        // 3. DTO 매핑 (항상 false)
        return missions.stream()
                .map(mission -> new MissionDto(
                        mission.getId(),
                        mission.getMissionType(),
                        mission.getDescription(),
                        false  // 오늘 완료 여부를 무조건 false로 고정
                ))
                .collect(Collectors.toList());
    }


    // 미션 완료
    public MissionDto.MissionBadgeResponseDto completeMission(Long userId, Long missionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        // 오늘 날짜의 시작 시간 (00:00)
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        double carbonReduction = mission.getCarbonReduction();
        String missionMessage;

        boolean alreadyCompleted = false;

        // boolean alreadyCompleted = userMissionRepository.existsByUserAndMissionAndCompletedAtAfter(user, mission, startOfDay);
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
    public String checkAttendance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Mission mission = missionRepository.findByMissionType(MissionType.ATTENDANCE)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        LocalDate today = LocalDate.now();
        boolean alreadyCompleted = userMissionRepository.existsByUserAndMissionAndCompletedAtAfter(user, mission, today.atStartOfDay());

        if (alreadyCompleted) {
            return "이미 출석을 했어요.";
        }
        return "매일 출석하고 물 받아요!";
    }

    // 걸음수
    public String checkSteps(StepDataDto stepDataDto) {
        User user = userRepository.findById(stepDataDto.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Mission mission = missionRepository.findByMissionType(MissionType.WALK)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

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

    // 뱃지 체크 및 부여
    public ResponseEntity<String> checkBadge(Long userId, Long missionId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        // 사용자가 해당 미션을 수행한 횟수를 조회
        long missionCount = userMissionRepository.countByUserAndMission(user, mission);

        // 미션 타입에 맞는 뱃지 타입을 가져오기
        BadgeType badgeType = mission.getMissionType().getBadgeType();

        // 미션 수행 횟수가 조건을 만족하는지 확인
        if (missionCount >= badgeType.getRequiredCount()) {
            // 뱃지 중복 인증
            Badge badge = badgeRepository.findByName(mission.getMissionType().getBadgeName())
                    .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

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
