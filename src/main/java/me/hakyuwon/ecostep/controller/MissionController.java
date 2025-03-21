package me.hakyuwon.ecostep.controller;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.dto.StepDataDto;
import me.hakyuwon.ecostep.dto.UserMissionDto;
import me.hakyuwon.ecostep.repository.MissionRepository;
import me.hakyuwon.ecostep.repository.UserMissionRepository;
import me.hakyuwon.ecostep.repository.UserRepository;
import me.hakyuwon.ecostep.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/missions")
public class MissionController {
    @Autowired
    private final MissionService missionService;
    private final UserMissionRepository userMissionRepository;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;

    // 미션 완료
    @PostMapping("/complete")
    public ResponseEntity<String> completeMission(@RequestBody UserMissionDto userMissionDto) {
        String result = missionService.completeMission(userMissionDto.getUserId(), userMissionDto.getMissionId());
        return ResponseEntity.ok(result);
    }

    // 출석 체크 미션
    @GetMapping("/attend/{userId}")
    public ResponseEntity<String> getAttendanceStatus(@PathVariable Long userId) {
        String result = missionService.checkAttendance(userId);
        return ResponseEntity.ok(result);
    }

    /* 영수증 인증 미션
    @PostMapping("/receipt")
    public ResponseEntity<String> receiptMission(@RequestBody UserMissionDto userMissionDto) {

    }*/

    // 텀블러 사용 미션

    // 3000보 이상 걷기
    @PostMapping("/walk")
    public ResponseEntity<String> walkMission(@RequestBody StepDataDto stepDto) {
        String result = missionService.checkSteps(stepDto);
        return ResponseEntity.ok(result);
    }

    // 각 뱃지 지급 api

    // 친환경 물품 구매

    // 친환경 기사 읽기
}
