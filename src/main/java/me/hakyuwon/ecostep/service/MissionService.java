package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.*;
import me.hakyuwon.ecostep.dto.DiaryDto;
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
    private final EcoDiaryRepository ecoDiaryRepository;

    // 물 주는 미션 ID 목록 (출석, 걷기, ox 퀴즈, 객관식 퀴즈)
    private static final Set<Long> WATER_MISSION_IDS = Set.of(1L, 3L, 5L, 6L);
    // 비료 주는 미션 ID 목록 (계단, 텀블러)
    private static final Set<Long> FERTILIZER_MISSION_IDS = Set.of(2L, 4L, 7l);
    private static final String QR_AUTHENTICATION_CODE = "STAIRMISSION_001_2025";
    private final BadgeRepository badgeRepository;

    // 미션 목록 조회
    public List<MissionDto> getAllMissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        List<Mission> missions = missionRepository.findAll();
        LocalDateTime startOfToday = LocalDate.now(koreaZone).atStartOfDay();

        Set<Long> completedMissionIds = userMissionRepository
                .findByUserAndCompletedAtAfter(user, startOfToday)
                .stream()
                .map(userMission -> userMission.getMission().getId())
                .collect(Collectors.toSet());

        return missions.stream()
                .map(mission -> {
                    boolean isCompleted = completedMissionIds.contains(mission.getId());

                    return new MissionDto(
                            mission.getId(),
                            mission.getMissionType(),
                            mission.getDescription(),
                            isCompleted
                    );
                })
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

        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        LocalDateTime startOfDay = LocalDate.now(koreaZone).atStartOfDay();
        double carbonReduction = mission.getCarbonReduction();
        String missionMessage;

        boolean alreadyCompleted = userMissionRepository.existsByUserAndMissionAndCompletedAtAfter(user, mission, startOfDay);
        if (!alreadyCompleted) {
            // 미션 완료 기록 생성
            UserMission userMission = UserMission.builder()
                    .user(user)
                    .mission(mission)
                    .carbonReduction(carbonReduction)
                    .completedAt(LocalDateTime.now(koreaZone))
                    .build();

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

        long missionCount = userMissionRepository.countByUserAndMission(user, mission);
        return new MissionDto.MissionBadgeResponseDto(missionCount, missionMessage);
    }

    // 미션 실패 (퀴즈용)
    public String failMission(Long userId, Long missionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        boolean alreadyCompleted = userMissionRepository.existsByUserAndMissionAndCompletedAtAfter(user, mission, startOfDay);

        if (!alreadyCompleted) {
            // 미션 완료 기록 생성
            UserMission userMission = UserMission.builder()
                    .user(user)
                    .mission(mission)
                    .carbonReduction(0)
                    .completedAt(LocalDateTime.now())
                    .build();

            userMissionRepository.save(userMission);

            return "미션에 실패했습니다. 다음 기회에 도전해주세요.";
        } else {
            return "이미 완료한 미션입니다.";
        }
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
    public String checkSteps(StepDataDto stepDataDto, User user) {

        Mission mission = missionRepository.findByMissionType(MissionType.WALK)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        LocalDate today = LocalDate.now();
        boolean alreadyCompleted = userMissionRepository.existsByUserAndMissionAndCompletedAtAfter(user, mission, today.atStartOfDay());

        int step = stepDataDto.getSteps();
        if (step >= 5000) {
            if (alreadyCompleted) {
                return "이미 걸음수 체크를 했어요.";
            }
            return "5000보 이상 걷고, 물 받아요!";
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

    // 계단 오르기 -> qr 인증 미션
    public String checkStairs(Long missionId, String qrCodeString) {
        if(!missionId.equals(4L)){
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        if (!QR_AUTHENTICATION_CODE.equals(qrCodeString)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return "QR 인증이 완료되었습니다.";
    }

    // 환경 일기 미션
    public ResponseEntity<String> keepEcoDiary(Long userId, Long missionId, DiaryDto dto) {
        if(!missionId.equals(7L)){
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));

        String content = dto.getContent();
        LocalDate today = LocalDate.now();

        boolean alreadyWrote = ecoDiaryRepository.existsByUserAndDate(user, today);
        if (alreadyWrote) {
            return ResponseEntity.ok("오늘 이미 환경 일기를 작성하였습니다.");
        }

        EcoDiary ecoDiary = new EcoDiary();
        ecoDiary.setContent(content);
        ecoDiary.setUser(user);
        ecoDiary.setDate(today);
        ecoDiaryRepository.save(ecoDiary);

        MissionDto.MissionBadgeResponseDto missionResult = completeMission(userId, missionId);
        String responseMessage = missionResult.getMissionMessage();
        return ResponseEntity.ok(responseMessage);
    }
}
