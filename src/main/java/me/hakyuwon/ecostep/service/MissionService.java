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
public class MissionService {

    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;
    private final UserRepository userRepository;
    private final TreeRepository treeRepository;
    private final EcoDiaryRepository ecoDiaryRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    private static final String QR_AUTHENTICATION_CODE = "STAIRMISSION_001_2025";
    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    // 미션 목록 조회
    public List<MissionDto> getAllMissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Mission> missions = missionRepository.findAll();
        LocalDateTime startOfToday = LocalDate.now(KOREA_ZONE).atStartOfDay();

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
    @Transactional
    public String completeMission(Long userId, Long missionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now(KOREA_ZONE);
        LocalDateTime startOfDay = LocalDate.now(KOREA_ZONE).atStartOfDay();

        boolean alreadyCompleted = userMissionRepository.existsByUserAndMissionAndCompletedAtAfter(user, mission, startOfDay);

        if (alreadyCompleted) {
            return "이미 완료한 미션입니다.";
        }

        UserMission userMission = UserMission.builder()
                .user(user)
                .mission(mission)
                .carbonReduction(mission.getCarbonReduction())
                .completedAt(now)
                .build();

        userMissionRepository.save(userMission);

        applyTreeReward(user, missionId);

        return "미션이 완료되었습니다.";
    }

    // 나무 정보 갱신
    public void applyTreeReward(User user, Long missionId) {
        Tree tree = treeRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        switch (mission.getMissionType().getRewardType()) {
            case WATER -> tree.applyItems(1, 0);
            case FERTILIZER -> tree.applyItems(0, 1);
            case NONE -> {
                return;
            }
        }

        treeRepository.save(tree);
    }

    // 미션 실패 (퀴즈용)
    public String failMission(Long userId, Long missionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        LocalDateTime startOfDay = LocalDate.now(KOREA_ZONE).atStartOfDay();

        boolean alreadyCompleted = userMissionRepository.existsByUserAndMissionAndCompletedAtAfter(user, mission, startOfDay);

        if (!alreadyCompleted) {
            // 미션 완료 기록 생성
            UserMission userMission = UserMission.builder()
                    .user(user)
                    .mission(mission)
                    .carbonReduction(0)
                    .completedAt(LocalDateTime.now(KOREA_ZONE))
                    .build();

            userMissionRepository.save(userMission);

            return "미션에 실패했습니다.";
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

        return completeMission(user.getId(), mission.getId());
    }

    // 걸음수
    public String checkSteps(StepDataDto stepDataDto, User user) {
        int steps = stepDataDto.getSteps();

        if (steps < 5000) {
            return "아직 걸음수가 모자라요!";
        }

        Mission mission = missionRepository.findByMissionType(MissionType.WALK)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        return completeMission(user.getId(), mission.getId());
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

    // 환경 일기 미션
    public String keepEcoDiary(Long userId, Long missionId, DiaryDto dto) {
        if(!missionId.equals(7L)){
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));

        String content = dto.getContent();
        LocalDate today = LocalDate.now(KOREA_ZONE);

        boolean alreadyWrote = ecoDiaryRepository.existsByUserAndDate(user, today);
        if (alreadyWrote) {
            return "오늘 이미 환경 일기를 작성하였습니다.";
        }

        EcoDiary ecoDiary = new EcoDiary();
        ecoDiary.setContent(content);
        ecoDiary.setUser(user);
        ecoDiary.setDate(today);
        ecoDiaryRepository.save(ecoDiary);

        return completeMission(userId, missionId);
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
}
