package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.Mission;
import me.hakyuwon.ecostep.domain.Tree;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.domain.UserMission;
import me.hakyuwon.ecostep.enums.MissionType;
import me.hakyuwon.ecostep.repository.MissionRepository;
import me.hakyuwon.ecostep.repository.TreeRepository;
import me.hakyuwon.ecostep.repository.UserMissionRepository;
import me.hakyuwon.ecostep.repository.UserRepository;
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

    // 물 주는 미션 ID 목록
    private static final Set<Long> WATER_MISSION_IDS = Set.of(1L, 2L, 3L, 4L);
    // 비료 주는 미션 ID 목록
    private static final Set<Long> FERTILIZER_MISSION_IDS = Set.of(5L, 6L);


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
        if (userMissionRepository.existsByUserAndMissionIdAndCompletedAtAfter(user, mission, startOfDay)) {
            return "이미 완료한 미션입니다.";
        }

        UserMission userMission = new UserMission();
        userMission.setUser(user);
        userMission.setMission(mission);
        userMission.setCompletedAt(LocalDateTime.now());

        if (WATER_MISSION_IDS.contains(missionId)) {
            tree.applyItems(1,0);
        } else if (FERTILIZER_MISSION_IDS.contains(missionId)) {
            tree.applyItems(0,1);
        }

        userMissionRepository.save(userMission);
        treeRepository.save(tree);
        return "미션 완료!";
    }
}
