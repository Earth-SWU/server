package me.hakyuwon.ecostep.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.domain.Mission;
import me.hakyuwon.ecostep.domain.User;
import me.hakyuwon.ecostep.dto.MissionDto;
import me.hakyuwon.ecostep.dto.UserMissionDto;
import me.hakyuwon.ecostep.enums.MissionType;
import me.hakyuwon.ecostep.repository.UserMissionRepository;
import me.hakyuwon.ecostep.service.MissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/missions")
public class MissionController {
    private final MissionService missionService;
    private final UserMissionRepository userMissionRepository;

    // 미션 완료
    @PostMapping("/complete")
    public ResponseEntity<String> completeMission(@RequestBody UserMissionDto userMissionDto) {
        String result = missionService.completeMission(userMissionDto.getUserId(), userMissionDto.getMissionId());
        return ResponseEntity.ok(result);
    }

    // 출석 체크 미션
    @GetMapping("/attend")
    public ResponseEntity<String> getAttendanceStatus(@RequestParam User user, @RequestParam Mission mission) {
        LocalDate today = LocalDate.now();
        boolean alreadyCompleted = userMissionRepository.existsByUserAndMissionIdAndCompletedAtAfter(user, mission, today.atStartOfDay());

        if (alreadyCompleted) {
            return ResponseEntity.ok("이미 출석을 했어요.");
        }
        return ResponseEntity.ok("매일 출석하고 물 받아요!");
    }

    // 영수증 인증 미션

    // 텀블러 사용 미션

    // 5000보 이상 걷기

    // 친환경 물품 구매

    // 친환경 기사 읽기
}
